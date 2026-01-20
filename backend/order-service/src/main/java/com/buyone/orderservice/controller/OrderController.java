package com.buyone.orderservice.controller;

import com.buyone.orderservice.model.Order;
import com.buyone.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;
import com.buyone.orderservice.model.OrderStatus;

import com.buyone.orderservice.dto.request.OrderSearchRequest;
import com.buyone.orderservice.dto.response.OrderResponse;
import com.buyone.orderservice.dto.response.OrderItemResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;  // For @ModelAttribute
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
// @Tag(name = "Orders", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")  // For Swagger/OpenAPI docs
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping("/checkout")
    @Operation(summary = "Create order from user's cart", description = "Converts authenticated user's cart to PENDING order")
    @PreAuthorize("hasRole('CLIENT')")  // Only buyers can checkout
    public ResponseEntity<Order> createOrderFromCart(Authentication auth) {
        String userId = auth.getName();  // JWT principal (userId)
        log.info("Creating order for client: {}", userId);
        Order order = orderService.createOrderFromCart(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    @GetMapping("/buyer")
    @Operation(summary = "Get buyer's orders", description = "List all orders for authenticated buyer")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<Order>> getBuyerOrders(Authentication auth) {
        String userId = auth.getName();
        List<Order> orders = orderService.getBuyerOrders(userId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderNumber}")
    @Operation(summary = "Get order details", description = "Fetch specific order by orderNumber")
    public ResponseEntity<Order> getOrder(@PathVariable @NotBlank String orderNumber) {
        Order order = orderService.getOrder(orderNumber);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderNumber}/status")
    @Operation(summary = "Update order status", description = "Seller updates status (e.g., SHIPPED, DELIVERED)")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Order> updateStatus(
            @PathVariable @NotBlank String orderNumber,
            @RequestParam @NotBlank String status,
            Authentication auth) {
        // TODO: Add ownership check - verify seller owns items in order
        log.info("Seller {} updating order {} to status: {}", auth.getName(), orderNumber, status);
        Order updated = orderService.updateStatus(orderNumber, status);
        return ResponseEntity.ok(updated);
    }
    
    @PostMapping("/{orderNumber}/cancel")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Cancel PENDING order")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderNumber, Authentication auth) {
        orderService.cancelOrder(orderNumber);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{orderNumber}/redo")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Redo CANCELLED order")
    public ResponseEntity<OrderResponse> redoOrder(@PathVariable String orderNumber, Authentication auth) {
        Order order = orderService.redoOrder(orderNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToOrderResponse(order));
    }
    
    @GetMapping("/buyer/search")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Page<OrderResponse>> searchMyOrders(
            @Valid @ModelAttribute OrderSearchRequest req, Authentication auth) {
        Page<Order> orders = orderService.searchBuyerOrders(auth.getName(), req);
        return ResponseEntity.ok(orders.map(this::mapToOrderResponse));
    }
    
    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<OrderResponse>> getSellerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, Authentication auth) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getSellerOrders(auth.getName(), pageable);
        return ResponseEntity.ok(orders.map(this::mapToOrderResponse));
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
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
                .status(OrderStatus.valueOf(order.getStatus()))  // Converts String → enum safely
                .total(order.getTotal())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
    
}
