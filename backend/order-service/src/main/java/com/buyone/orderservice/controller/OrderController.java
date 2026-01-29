package com.buyone.orderservice.controller;

import com.buyone.orderservice.dto.request.order.CreateOrderRequest;
import com.buyone.orderservice.dto.request.order.OrderSearchRequest;
import com.buyone.orderservice.dto.response.order.OrderResponse;
import com.buyone.orderservice.dto.response.order.OrderItemResponse;
import com.buyone.orderservice.model.order.Order;
import com.buyone.orderservice.model.order.OrderStatus;
import com.buyone.orderservice.service.OrderService;
import com.buyone.orderservice.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping("/checkout")
    @Operation(summary = "Create order from cart", description = "Pay on Delivery")
    public ResponseEntity<Order> createOrderFromCart(
            @Valid @RequestBody CreateOrderRequest req,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "CLIENT") String role) {
        validateRole(role, "CLIENT");
        log.info("Client {} checking out with address", userId);
        Order order = orderService.createOrderFromCart(userId, req.getShippingAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    @GetMapping("/buyer")
    @Operation(summary = "Get buyer orders")
    public ResponseEntity<List<OrderResponse>> getBuyerOrders(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "CLIENT") String role) {
        validateRole(role, "CLIENT");
        List<OrderResponse> orders = orderService.getBuyerOrders(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderNumber}")
    @Operation(summary = "Get order details")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderNumber) {
        OrderResponse order = orderService.getOrder(orderNumber)
                .map(this::mapToOrderResponse)
                .orElseThrow(() -> new BadRequestException("Order not found: " + orderNumber));
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderNumber}/status")
    @Operation(summary = "Update order status", description = "Seller: PENDING→CONFIRMED→SHIPPED→DELIVERED")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable String orderNumber,
            @RequestParam OrderStatus status,
            @RequestHeader("X-USER-ID") String sellerId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "SELLER") String role) {
        validateRole(role, "SELLER");
        log.info("Seller {} updating order {} to {}", sellerId, orderNumber, status);
        OrderResponse updated = orderService.updateStatus(orderNumber, sellerId, status)
                .map(this::mapToOrderResponse)
                .orElseThrow(() -> new BadRequestException("Order not found or update failed: " + orderNumber));
        return ResponseEntity.ok(updated);
    }
    
    @PostMapping("/{orderNumber}/cancel")
    @Operation(summary = "Cancel PENDING order")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable String orderNumber,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "CLIENT") String role) {
        validateRole(role, "CLIENT");
        orderService.cancelOrder(orderNumber, userId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{orderNumber}/redo")
    @Operation(summary = "Redo CANCELLED order")
    public ResponseEntity<OrderResponse> redoOrder(
            @PathVariable String orderNumber,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "CLIENT") String role) {
        validateRole(role, "CLIENT");
        OrderResponse newOrder = orderService.redoOrder(orderNumber, userId)
                .map(this::mapToOrderResponse)
                .orElseThrow(() -> new BadRequestException("Order not found: " + orderNumber));
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }
    
    @GetMapping("/buyer/search")
    @Operation(summary = "Search buyer orders")
    public ResponseEntity<Page<OrderResponse>> searchMyOrders(
            @Valid @ModelAttribute OrderSearchRequest req,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "CLIENT") String role) {
        validateRole(role, "CLIENT");
        Page<OrderResponse> orders = orderService.searchBuyerOrders(userId, req)
                .map(this::mapToOrderResponse);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/seller")
    @Operation(summary = "Get seller orders", description = "Paginated dashboard")
    public ResponseEntity<Page<OrderResponse>> getSellerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-USER-ID") String sellerId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "SELLER") String role) {
        validateRole(role, "SELLER");
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> orders = orderService.getSellerOrders(sellerId, pageable)
                .map(this::mapToOrderResponse);
        return ResponseEntity.ok(orders);
    }
    
    private void validateRole(String role, String requiredRole) {
        if (!requiredRole.equals(role)) {
            throw new BadRequestException("Required role: " + requiredRole + ", got: " + role);
        }
    }
    
    private OrderResponse mapToOrderResponse(com.buyone.orderservice.model.order.Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productName(item.getProductName())
                        .sellerId(item.getSellerId())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());
        
        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .total(order.getTotal())
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .createdAt(order.getCreatedAt())
                .shippingAddress(order.getShippingAddress())
                .items(items)
                .build();
    }
    
}
