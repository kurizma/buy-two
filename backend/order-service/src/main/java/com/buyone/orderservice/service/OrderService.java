package com.buyone.orderservice.service;

import com.buyone.orderservice.dto.request.order.OrderSearchRequest;
import com.buyone.orderservice.model.Address;
import com.buyone.orderservice.model.order.Order;
import com.buyone.orderservice.model.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrderFromCart(String userId, Address shippingAddress);
    
    List<Order> getBuyerOrders(String userId);
    
    Optional<Order> getOrder(String orderNumber);
    
    Optional<Order> updateStatus(String orderNumber, String sellerId, OrderStatus status);
    
    Page<Order> searchBuyerOrders(String userId, OrderSearchRequest req);
    
    Page<Order> getSellerOrders(String sellerId, Pageable pageable);
    
    Optional<Order> confirmOrder(String orderNumber, String userId);
    
    void cancelOrder(String orderNumber, String userId);  // Only PENDING → CANCELLED
    
    Optional<Order> redoOrder(String orderNumber, String userId);   // CANCELLED → new cart → new order
}
