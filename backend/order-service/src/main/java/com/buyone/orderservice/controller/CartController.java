package com.buyone.orderservice.controller;

import com.buyone.orderservice.dto.response.ApiResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    
    private static final String CLIENT_ROLE = "CLIENT";
    private final CartService cartService;
    
    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Merge if same product+seller")
    public ResponseEntity<ApiResponse<Cart>> addItem(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role,
            @Valid @RequestBody CartItem item) {
        validateRole(role, CLIENT_ROLE);
        log.debug("Adding item to cart for user: {}", userId);
        Cart cart = cartService.addItem(userId, item);
        return ResponseEntity.ok(ApiResponse.<Cart>builder()
                .success(true)
                .message("Item added to cart successfully")
                .data(cart)
                .build());
    }
    
    @GetMapping
    @Operation(summary = "Get current cart")
    public ResponseEntity<ApiResponse<Optional<Cart>>> getCart(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role) {
        
        Optional<Cart> cartOpt = cartService.getCart(userId);
        if (cartOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.<Optional<Cart>>builder()
                    .success(true)
                    .message("Cart is empty")
                    .data(Optional.empty())
                    .build());
        }
        return ResponseEntity.ok(ApiResponse.<Optional<Cart>>builder()
                .success(true)
                .message("Cart fetched successfully")
                .data(cartOpt)
                .build());
    }
    
    @PutMapping("/items/{productId}/quantity/{quantity}")
    @Operation(summary = "Update item quantity")
    public ResponseEntity<ApiResponse<Cart>> updateQuantity(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role,
            @PathVariable String productId,
            @PathVariable int quantity) {
        validateRole(role, CLIENT_ROLE);
        Cart cart = cartService.updateQuantity(userId, productId, quantity);
        return ResponseEntity.ok(ApiResponse.<Cart>builder()
                .success(true)
                .message("Item quantity updated successfully")
                .data(cart)
                .build());
    }
    
    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role,
            @PathVariable String productId) {
        validateRole(role, CLIENT_ROLE);
        cartService.removeItem(userId, productId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Item removed from cart successfully")
                .build());
    }
    
    @DeleteMapping
    @Operation(summary = "Clear entire cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-USER-ROLE") String role) {
        validateRole(role, CLIENT_ROLE);
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Cart cleared successfully")
                .build());
    }
    
    private void validateRole(String role, String requiredRole) {
        if (!requiredRole.equals(role)) {
            throw new BadRequestException("Required role: " + requiredRole + ", got: " + role);
        }
    }
}
