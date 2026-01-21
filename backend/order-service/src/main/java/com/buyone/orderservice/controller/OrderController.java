package com.buyone.orderservice.controller;

import com.buyone.orderservice.dto.request.CreateOrderRequest;  // NEW
import com.buyone.orderservice.dto.request.OrderSearchRequest;
import com.buyone.orderservice.dto.response.OrderResponse;
import com.buyone.orderservice.dto.response.OrderItemResponse;
import com.buyone.orderservice.model.Address;
import com.buyone.orderservice.model.Order;
import com.buyone.orderservice.model.OrderStatus;
import com.buyone.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderController - REST APIs for cart→checkout, order management, seller dashboards.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * ✅ CHANGED: Now accepts shipping address for checkout.
     * Re-fetches fresh product data for legal accuracy.
     */
    @PostMapping("/checkout")
    @Operation(summary = "Create order from user's cart",
            description = "Converts authenticated user's cart to PENDING order with fresh product snapshots")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<OrderResponse> createOrderFromCart(
            @Valid @RequestBody CreateOrderRequest req,  // ✅ NEW DTO with Address
            Authentication auth) {
        
        String userId = auth.getName();
        log.info("Client {} checking out (items: ?)", userId);
        
        Order order = orderService.createOrderFromCart(userId, req.getShippingAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToOrderResponse(order));
    }
    
    @GetMapping("/buyer")
    @Operation(summary = "Get buyer's orders")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<OrderResponse>> getBuyerOrders(Authentication auth) {
        String userId = auth.getName();
        List<Order> orders = orderService.getBuyerOrders(userId);
        return ResponseEntity.ok(orders.stream().map(this::mapToOrderResponse).toList());
    }
    
    @GetMapping("/{orderNumber}")
    @Operation(summary = "Get order details")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable @NotBlank String orderNumber) {
        Order order = orderService.getOrder(orderNumber);
        return ResponseEntity.ok(mapToOrderResponse(order));
    }
    
    /**
     * ✅ CHANGED: Now accepts OrderStatus enum (type-safe).
     */
    @PutMapping("/{orderNumber}/status")
    @Operation(summary = "Update order status", description = "Seller: PENDING→CONFIRMED→SHIPPED→DELIVERED")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable @NotBlank String orderNumber,
            @RequestParam OrderStatus status,  // ✅ Enum - Swagger dropdown!
            Authentication auth) {
        
        // TODO: Ownership check (seller owns items.sellerId)
        log.info("Seller {} → order {}: {}", auth.getName(), orderNumber, status);
        Order updated = orderService.updateStatus(orderNumber, status);
        return ResponseEntity.ok(mapToOrderResponse(updated));
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
    @Operation(summary = "Redo CANCELLED order → new cart + fresh snapshots")
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
    
    /**
     * Maps Order → OrderResponse (hides internal fields).
     */
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
                .status(order.getStatus())  // ✅ Already OrderStatus enum
                .total(order.getTotal())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
