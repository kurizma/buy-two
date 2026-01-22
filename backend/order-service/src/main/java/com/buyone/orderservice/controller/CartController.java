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
@SecurityRequirement(name = "bearerAuth")
public class CartController {
    
    private final CartService cartService;
    
    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Merge if same product+seller")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Cart> addItem(Authentication auth,
                                        @Valid @RequestBody CartItem item) {
        String userId = getUserIdFromAuth(auth);
        log.debug("Adding item to cart for user: {}", userId);
        Cart cart = cartService.addItem(userId, item);
        return ResponseEntity.ok(cart);
    }
    
    @GetMapping
    @Operation(summary = "Get current cart")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Cart> getCart(Authentication auth) {
        String userId = getUserIdFromAuth(auth);
        return cartService.getCart(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    
    @PutMapping("/items/{productId}/quantity/{quantity}")
    @Operation(summary = "Update item quantity")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Cart> updateQuantity(Authentication auth,
                                               @PathVariable String productId,
                                               @PathVariable int quantity) {
        String userId = getUserIdFromAuth(auth);
        Cart cart = cartService.updateQuantity(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> removeItem(Authentication auth,
                                           @PathVariable String productId) {
        String userId = getUserIdFromAuth(auth);
        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping
    @Operation(summary = "Clear entire cart")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> clearCart(Authentication auth) {
        String userId = getUserIdFromAuth(auth);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
    
    private String getUserIdFromAuth(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        if (userId == null || userId.trim().isEmpty()) {
            throw new BadRequestException("User ID not found in authentication");
        }
        return userId;
    }
}
