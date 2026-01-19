package com.buyone.orderservice.service.impl;

import com.buyone.orderservice.exception.ResourceNotFoundException;
import com.buyone.orderservice.model.CartItem;
import com.buyone.orderservice.model.Order;
import com.buyone.orderservice.model.OrderItem;
import com.buyone.orderservice.service.CartService;
import com.buyone.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.buyone.orderservice.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final CartService cartService;
    
    @Override
    public Order createOrderFromCart(String userId) {
        var cartOpt = cartService.getCart(userId);
        var cart = cartOpt.orElseThrow(() ->
                new ResourceNotFoundException("Cart not found for user: " + userId));
        
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }
        
        var orderItems = cart.getItems().stream()
                .map(this::toOrderItem)
                .collect(Collectors.toList());
        
        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        Order order = Order.builder()
                .userId(userId)
                .orderNumber(orderNumber)
                .items(orderItems)
                .status("PENDING")  // ✅ String for your Order.java
                .subtotal(cart.getSubtotal())
                .tax(cart.getTax())
                .total(cart.getTotal())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Order saved = orderRepository.save(order);
        cartService.clearCart(userId);
        log.info("Order created: {} for user: {}", orderNumber, userId);
        return saved;
    }
    
    private OrderItem toOrderItem(CartItem cartItem) {
        return OrderItem.builder()
                .productId(cartItem.getProductId())
                .productName(cartItem.getProductName())
                .sellerId(cartItem.getSellerId())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .imageUrl(cartItem.getImageUrl())
                .build();
    }
    
    @Override
    public List<Order> getBuyerOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }
    
    @Override
    public Order getOrder(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
    }
    
    @Override
    public Order updateStatus(String orderNumber, String status) {
        Order order = getOrder(orderNumber);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}
