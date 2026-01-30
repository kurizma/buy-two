package com.buyone.orderservice.service;

import com.buyone.orderservice.model.cart.Cart;
import com.buyone.orderservice.model.cart.CartItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import java.util.Optional;

public interface CartService {
    Cart addItem(@NotBlank String userId, CartItem item);
    Optional<Cart> getCart(String userId);
    Cart updateQuantity(@NotBlank String userId, @NotBlank String productId, @Min(1) int quantity);
    Cart removeItem(@NotBlank String userId, String productId);
    Cart clearCart(@NotBlank String userId);
    void saveCart(Cart cart);
    
}