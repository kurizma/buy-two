package com.buyone.orderservice.controller;

import com.buyone.orderservice.dto.request.order.CreateOrderRequest;
import com.buyone.orderservice.dto.request.order.OrderSearchRequest;
import com.buyone.orderservice.dto.response.ApiResponse;
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

import java.math.BigDecimal;
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
    public ResponseEntity<ApiResponse<OrderResponse>> createOrderFromCart(
            @Valid @RequestBody CreateOrderRequest req,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role) {
        validateRole(role, "CLIENT");
        log.info("Client {} checking out with address", userId);
        Order order = orderService.createOrderFromCart(userId, req.getShippingAddress());
        OrderResponse orderResp = mapToOrderResponse(order);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<OrderResponse>builder()
                        .success(true)
                        .message("Order created successfully")
                        .data(orderResp)
                        .build());
    }
    
    @GetMapping("/buyer")
    @Operation(summary = "Get buyer orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getBuyerOrders(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role) {
        validateRole(role, "CLIENT");
        List<OrderResponse> orders = orderService.getBuyerOrders(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .message("Buyer orders fetched successfully")
                .data(orders)
                .build());
    }
    
    @GetMapping("/{orderNumber}")
    @Operation(summary = "Get order details")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable String orderNumber) {
        OrderResponse order = orderService.getOrder(orderNumber)
                .map(this::mapToOrderResponse)
                .orElseThrow(() -> new BadRequestException("Order not found: " + orderNumber));
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order details fetched successfully")
                .data(order)
                .build());
    }
    
    @PutMapping("/{orderNumber}/status")
    @Operation(summary = "Update order status", description = "Seller: PENDING→CONFIRMED→SHIPPED→DELIVERED")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable String orderNumber,
            @RequestParam OrderStatus status,
            @RequestHeader("X-USER-ID") String sellerId,
            @RequestHeader("X-USER-ROLE") String role) {
        validateRole(role, "SELLER");
        log.info("Seller {} updating order {} to {}", sellerId, orderNumber, status);
        OrderResponse updated = orderService.updateStatus(orderNumber, sellerId, status)
                .map(this::mapToOrderResponse)
                .orElseThrow(() -> new BadRequestException("Order not found or update failed: " + orderNumber));
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order status updated successfully")
                .data(updated)
                .build());
    }
    
    @PostMapping("/{orderNumber}/confirm")
    @Operation(summary = "Seller confirms PENDING order", description = "Pay on Delivery final step")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmOrder(
            @PathVariable String orderNumber,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role) {
        validateRole(role, "SELLER");
        
        OrderResponse confirmed = orderService.confirmOrder(orderNumber, userId)
                .map(this::mapToOrderResponse)
                .orElseThrow(() -> new BadRequestException("Order not found or cannot confirm"));
        
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order confirmed successfully!")
                .data(confirmed)
                .build());
    }
    
    
    
    @PostMapping("/{orderNumber}/cancel")
    @Operation(summary = "Cancel PENDING order")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable String orderNumber,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role) {
        validateRole(role, "CLIENT");
        orderService.cancelOrder(orderNumber, userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Order cancelled successfully")
                .build());
    }
    
    @PostMapping("/{orderNumber}/redo")
    @Operation(summary = "Redo CANCELLED order")
    public ResponseEntity<ApiResponse<OrderResponse>> redoOrder(
            @PathVariable String orderNumber,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role) {
        validateRole(role, "CLIENT");
        OrderResponse newOrder = orderService.redoOrder(orderNumber, userId)
                .map(this::mapToOrderResponse)
                .orElseThrow(() -> new BadRequestException("Order not found: " + orderNumber));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<OrderResponse>builder()
                        .success(true)
                        .message("Order recreated successfully")
                        .data(newOrder)
                        .build());
    }
    
    @GetMapping("/buyer/search")
    @Operation(summary = "Search buyer orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> searchMyOrders(
            @Valid @ModelAttribute OrderSearchRequest req,
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role) {
        validateRole(role, "CLIENT");
        Page<OrderResponse> orders = orderService.searchBuyerOrders(userId, req)
                .map(this::mapToOrderResponse);
        return ResponseEntity.ok(ApiResponse.<Page<OrderResponse>>builder()
                .success(true)
                .message("Buyer orders search completed")
                .data(orders)
                .build());
    }
    
    @GetMapping("/seller")
    @Operation(summary = "Get seller orders", description = "Paginated dashboard")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getSellerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-USER-ID") String sellerId,
            @RequestHeader("X-USER-ROLE") String role) {
        validateRole(role, "SELLER");
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> orders = orderService.getSellerOrders(sellerId, pageable)
                .map(this::mapToOrderResponse);
        return ResponseEntity.ok(ApiResponse.<Page<OrderResponse>>builder()
                .success(true)
                .message("Seller orders fetched successfully")
                .data(orders)
                .build());
    }
    
    private void validateRole(String role, String requiredRole) {
        if (!requiredRole.equals(role)) {
            throw new BadRequestException("Required role: " + requiredRole + ", got: " + role);
        }
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productName(item.getProductName())
                        .sellerId(item.getSellerId())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .imageUrl(item.getImageUrl())
                        .build())
                .collect(Collectors.toList());
        
        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .total(order.getTotal())
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .shippingCost(order.getShippingCost() != null ? order.getShippingCost() : BigDecimal.valueOf(4.9))
                .createdAt(order.getCreatedAt())
                .shippingAddress(order.getShippingAddress())
                .items(items)
                .build();
    }
}
