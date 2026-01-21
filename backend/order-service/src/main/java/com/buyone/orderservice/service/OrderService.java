package com.buyone.orderservice.service;

import com.buyone.orderservice.dto.request.OrderSearchRequest;
import com.buyone.orderservice.model.Address;
import com.buyone.orderservice.model.Order;
import com.buyone.orderservice.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * OrderService interface - manages full order lifecycle with fresh snapshots.
 */
public interface OrderService {
    /**
     * Creates order from cart with FRESH product snapshots + shipping address.
     * Re-fetches live prices/names for legal accuracy.
     */
    Order createOrderFromCart(String userId, Address shippingAddress);
    
    List<Order> getBuyerOrders(String userId);
    
    Order getOrder(String orderNumber);
    
    /**
     * Updates order status (seller/admin) - PENDING→CONFIRMED→SHIPPED→DELIVERED.
     * TODO: Add state transition validation.
     */
    Order updateStatus(String orderNumber, OrderStatus status);
    
    Page<Order> searchBuyerOrders(String userId, OrderSearchRequest req);
    
    Page<Order> getSellerOrders(String sellerId, Pageable pageable);
    
    void cancelOrder(String orderNumber);  // Only PENDING → CANCELLED
    
    Order redoOrder(String orderNumber);   // CANCELLED → new cart → new order
}
