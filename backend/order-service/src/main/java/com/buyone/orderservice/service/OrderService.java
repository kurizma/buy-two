package com.buyone.orderservice.service;

import com.buyone.orderservice.model.Order;

import java.util.List;

public interface OrderService {
    Order createOrderFromCart(String userId);  // From checkout
    List<Order> getBuyerOrders(String userId);
    Order getOrder(String orderNumber);
    Order updateStatus(String orderNumber, String status);  // Seller use
}
