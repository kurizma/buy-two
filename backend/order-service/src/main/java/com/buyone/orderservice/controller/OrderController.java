package com.buyone.orderservice.controller;

import com.buyone.orderservice.model.Order;
import com.buyone.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")  // For Swagger/OpenAPI docs
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping("/checkout")
    @Operation(summary = "Create order from user's cart", description = "Converts authenticated user's cart to PENDING order")
    @PreAuthorize("hasRole('USER')")  // Only buyers can checkout
    public ResponseEntity<Order> createOrderFromCart(Authentication auth) {
        String userId = auth.getName();  // JWT principal (userId)
        log.info("Creating order for user: {}", userId);
        Order order = orderService.createOrderFromCart(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    @GetMapping("/buyer")
    @Operation(summary = "Get buyer's orders", description = "List all orders for authenticated buyer")
    @PreAuthorize("hasRole('USER')")
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
}
