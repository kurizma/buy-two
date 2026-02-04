package com.buyone.orderservice.service.impl;

import com.buyone.orderservice.client.ProductClient;
import com.buyone.orderservice.exception.BadRequestException;
import com.buyone.orderservice.model.Product;
import com.buyone.orderservice.model.cart.Cart;
import com.buyone.orderservice.model.cart.CartItem;
import com.buyone.orderservice.dto.response.ProductResponse;
import com.buyone.orderservice.dto.response.ApiResponse;
import com.buyone.orderservice.repository.CartRepository;
import com.buyone.orderservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Value;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated  // For method-level validation
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    private final ProductClient productClient;  //  Fixed: no @Autowired
    
    @Value("${app.cart.tax-rate:0.24}")  //  Configurable
    private double taxRate;
    
    @Override
    @Transactional  // Consistency guarantee
    public Cart addItem(String userId, CartItem item) {
        validateCartItem(item);
        Cart cart = getOrCreateCart(userId);
        
        // Merge if exists (multi-seller support)
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(ci -> ci.getProductId().equals(item.getProductId())
                        && ci.getSellerId().equals(item.getSellerId()))
                .findFirst();
        
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + item.getQuantity());
        } else {
            // Production integration - Full ApiResponse handling
            ApiResponse<ProductResponse> response = productClient.getById(item.getProductId());
            
            if (!response.isSuccess() || response.getData() == null) {
                log.warn("Product not found: {}", item.getProductId());
                throw new BadRequestException("Product not found: " + item.getProductId());
            }
            
            ProductResponse product = response.getData();
            
            // Debug logs (keep for now)
            log.info("Product: {} quantity={}, request qty={}",
                    product.getId(),
                    product.getQuantity(),
                    item.getQuantity());
            
            int availableStock = Optional.ofNullable(product.getQuantity()).orElse(0);
            log.info("Available: {}, requested: {}", availableStock, item.getQuantity());
            
            if (availableStock < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock: " + item.getQuantity() +
                        " requested, " + availableStock + " available");
            }
            
            // Populate cart item snapshot
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setImageUrl(product.getImages() != null && !product.getImages().isEmpty()
                    ? product.getImages().get(0) : null);
            
            cart.getItems().add(item);
        }
        return recalculateTotals(cart);
    }
    
    @Override
    public Optional<Cart> getCart(String userId) {
        return cartRepository.findById(userId);
    }
    
    @Override
    @Transactional
    public Cart updateQuantity(String userId, String productId,
                               int quantity) {
        Cart cart = getOrCreateCart(userId);
        boolean updated = cart.getItems().stream()
                .filter(ci -> ci.getProductId().equals(productId))
                .findFirst()
                .map(ci -> {
                    ci.setQuantity(quantity);
                    return true;
                }).orElse(false);
        
        if (!updated) {
            throw new BadRequestException("Product not found in cart: " + productId);
        }
        
        cart.getItems().removeIf(ci -> ci.getQuantity() <= 0);
        return recalculateTotals(cart);
    }
    
    @Override
    @Transactional
    public Cart removeItem(String userId, String productId) {
        Cart cart = getOrCreateCart(userId);
        boolean removed = cart.getItems().removeIf(ci -> ci.getProductId().equals(productId));
        if (!removed) {
            log.warn("Item not found in cart: user={}, product={}", userId, productId);
        }
        return recalculateTotals(cart);
    }
    
    @Override
    @Transactional
    public Cart clearCart(String userId) {
        return cartRepository.save(Cart.builder()
                .id(userId)
                .userId(userId)
                .items(new ArrayList<>())
                .subtotal(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .updatedAt(LocalDateTime.now())
                .build());
    }
    
    private Cart getOrCreateCart(String userId) {
        return cartRepository.findById(userId)
                .orElse(Cart.builder()
                        .id(userId)
                        .userId(userId)
                        .items(new ArrayList<>())
                        .build());
    }
    
    private Cart recalculateTotals(Cart cart) {
        // Total price (already incl VAT from product)
        BigDecimal totalInclVat = cart.getItems().stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // VAT = 24% of excl VAT price → reverse calculate
        // totalInclVat = subtotal * 1.24 → subtotal = totalInclVat / 1.24
        BigDecimal subtotalExclVat = totalInclVat.divide(BigDecimal.valueOf(1.24), 2, RoundingMode.HALF_UP);
        
        // VAT amount = totalInclVat - subtotalExclVat
        BigDecimal vatAmount = totalInclVat.subtract(subtotalExclVat);
        
        cart.setSubtotal(subtotalExclVat);    // €17.74
        cart.setTax(vatAmount);               // €4.26  
        cart.setTotal(totalInclVat);          // €22.00
        cart.setUpdatedAt(LocalDateTime.now());
        
        return cartRepository.save(cart);
    }

    
    private void validateCartItem(CartItem item) {
        if (item.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be positive");
        }
        if (item.getProductId() == null || item.getProductId().trim().isEmpty()) {
            throw new BadRequestException("Product ID is required");
        }
        if (item.getSellerId() == null || item.getSellerId().trim().isEmpty()) {
            throw new BadRequestException("Seller ID is required");
        }
    }
    @Override
    @Transactional
    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }
    
}
