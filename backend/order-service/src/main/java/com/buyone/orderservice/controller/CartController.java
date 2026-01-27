package com.buyone.orderservice.controller;

import com.buyone.orderservice.model.cart.Cart;
import com.buyone.orderservice.model.cart.CartItem;
import com.buyone.orderservice.service.CartService;
import com.buyone.orderservice.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    
    private static final String CLIENT_ROLE = "CLIENT";
    private final CartService cartService;
    
    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Merge if same product+seller")
    public ResponseEntity<Cart> addItem(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "CLIENT") String role,
            @Valid @RequestBody CartItem item) {
        validateRole(role, CLIENT_ROLE);
        log.debug("Adding item to cart for user: {}", userId);
        Cart cart = cartService.addItem(userId, item);
        return ResponseEntity.ok(cart);
    }
    
    @GetMapping
    @Operation(summary = "Get current cart")
    public ResponseEntity<Cart> getCart(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "CLIENT") String role) {
        validateRole(role, CLIENT_ROLE);
        return cartService.getCart(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    
    @PutMapping("/items/{productId}/quantity/{quantity}")
    @Operation(summary = "Update item quantity")
    public ResponseEntity<Cart> updateQuantity(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "CLIENT") String role,
            @PathVariable String productId,
            @PathVariable int quantity) {
        validateRole(role, CLIENT_ROLE);
        Cart cart = cartService.updateQuantity(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<Void> removeItem(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "CLIENT") String role,
            @PathVariable String productId) {
        validateRole(role, CLIENT_ROLE);
        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping
    @Operation(summary = "Clear entire cart")
    public ResponseEntity<Void> clearCart(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-USER-ROLE", defaultValue = "CLIENT") String role) {
        validateRole(role, CLIENT_ROLE);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
    
    private void validateRole(String role, String requiredRole) {
        if (!requiredRole.equals(role)) {
            throw new BadRequestException("Required role: " + requiredRole + ", got: " + role);
        }
    }
}
    

