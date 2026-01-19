package com.buyone.orderservice.controller;

import com.buyone.orderservice.model.Cart;
import com.buyone.orderservice.model.CartItem;
import com.buyone.orderservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(
            Authentication auth,  // Extracts userId from JWT principal
            @Valid @RequestBody CartItem item) {
        String userId = getUserIdFromAuth(auth);
        return ResponseEntity.ok(cartService.addItem(userId, item));
    }
    
    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication auth) {
        String userId = getUserIdFromAuth(auth);
        return cartService.getCart(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());  // 204 for empty cart (better UX)
    }
    
    @PutMapping("/items/{productId}/quantity/{quantity}")
    public ResponseEntity<Cart> updateQuantity(Authentication auth,
                                               @PathVariable String productId,
                                               @PathVariable int quantity) {
        String userId = getUserIdFromAuth(auth);
        return ResponseEntity.ok(cartService.updateQuantity(userId, productId, quantity));
    }
    
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(Authentication auth, @PathVariable String productId) {
        String userId = getUserIdFromAuth(auth);
        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication auth) {
        String userId = getUserIdFromAuth(auth);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(Authentication auth) {
        String userId = getUserIdFromAuth(auth);
        Cart cart = cartService.getCart(userId).orElseThrow(() ->
                new IllegalStateException("Cart not found"));
        
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout empty cart");
        }
        
        cartService.clearCart(userId);
        return ResponseEntity.ok("Order created Sucessfully with Pay on Delivery");
    }
    
    private String getUserIdFromAuth(Authentication auth) {
        return auth.getName();  // Assumes JWT principal has username as userId
        // Or: ((UserDetails) auth.getPrincipal()).getId() if custom
    }
}
