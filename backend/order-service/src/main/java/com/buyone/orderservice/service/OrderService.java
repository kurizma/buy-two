package com.buyone.orderservice.service;

import com.buyone.orderservice.dto.request.OrderSearchRequest;
import com.buyone.orderservice.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    Order createOrderFromCart(String userId);  // From checkout
    List<Order> getBuyerOrders(String userId);
    Order getOrder(String orderNumber);
    Order updateStatus(String orderNumber, String status);  // Seller use
    
    Page<Order> searchBuyerOrders(String userId, OrderSearchRequest req);
    Page<Order> getSellerOrders(String sellerId, Pageable pageable);
    void cancelOrder(String orderNumber);  // If PENDING → CANCELLED
    Order redoOrder(String orderNumber);   // Clone items to new cart/order
}
