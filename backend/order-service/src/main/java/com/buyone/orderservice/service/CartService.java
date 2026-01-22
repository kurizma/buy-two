package com.buyone.orderservice.service;

import com.buyone.orderservice.model.cart.Cart;
import com.buyone.orderservice.model.cart.CartItem;
import java.util.Optional;

public interface CartService {
    Cart addItem(String userId, CartItem item);
    Optional<Cart> getCart(String userId);
    Cart updateQuantity(String userId, String productId, int quantity);
    Cart removeItem(String userId, String productId);
    Cart clearCart(String userId);
    void saveCart(Cart cart);  // Persists/updates cart
    
}