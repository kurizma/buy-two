package com.buyone.orderservice.service.impl;

import com.buyone.orderservice.exception.BadRequestException;
import com.buyone.orderservice.model.Cart;
import com.buyone.orderservice.model.CartItem;
import com.buyone.orderservice.repository.CartRepository;
import com.buyone.orderservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private static final double TAX_RATE = 0.1; // Configurable via @Value later
    
    @Override
    public Cart addItem(String userId, CartItem item) {
        validateCartItem(item);
        Cart cart = getOrCreateCart(userId);
        // Merge if exists (best practice: no duplicates by productId+sellerId)
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(ci -> ci.getProductId().equals(item.getProductId())
                        && ci.getSellerId().equals(item.getSellerId()))
                .findFirst();
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + item.getQuantity());
        } else {
            // TODO: FeignClient/RestTemplate to product-service: validate stock >= quantity
            cart.getItems().add(item);
        }
        return updateCart(cart);
    }
    
    @Override
    public Optional<Cart> getCart(String userId) {
        return cartRepository.findById(userId);
    }
    
    @Override
    public Cart updateQuantity(String userId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        Cart cart = getOrCreateCart(userId);
        cart.getItems().stream()
                .filter(ci -> ci.getProductId().equals(productId))
                .findFirst()
                .ifPresent(ci -> ci.setQuantity(quantity));
        cart.getItems().removeIf(ci -> ci.getQuantity() <= 0); // Auto-remove invalid
        return updateCart(cart);
    }
    
    @Override
    public Cart removeItem(String userId, String productId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(ci -> ci.getProductId().equals(productId));
        return updateCart(cart);
    }
    
    @Override
    public Cart clearCart(String userId) {
        return cartRepository.save(new Cart(userId, new ArrayList<>(), 0, 0, 0, LocalDateTime.now()));
    }
    
    private Cart getOrCreateCart(String userId) {
        return cartRepository.findById(userId).orElse(Cart.builder().id(userId).items(new ArrayList<>()).build());
    }
    
    private Cart updateCart(Cart cart) {
        cart.setSubtotal(cart.getItems().stream().mapToDouble(ci -> ci.getPrice() * ci.getQuantity()).sum());
        cart.setTax(cart.getSubtotal() * TAX_RATE);
        cart.setTotal(cart.getSubtotal() + cart.getTax());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
    
    private void validateCartItem(CartItem item) {
        if (item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (item.getProductId() == null || item.getProductId().trim().isEmpty()) {
            throw new BadRequestException("Product ID is required");
        }
    }
    
}
