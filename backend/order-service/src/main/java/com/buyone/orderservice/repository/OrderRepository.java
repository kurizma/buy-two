package com.buyone.orderservice.repository;

import com.buyone.orderservice.model.Order;
import com.buyone.orderservice.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);           // Buyer orders
    List<Order> findByUserIdAndStatus(String userId, OrderStatus status);
    Optional<Order> findByOrderNumber(String orderNumber);
}
