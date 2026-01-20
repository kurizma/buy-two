package com.buyone.orderservice.repository;

import com.buyone.orderservice.model.Order;
import com.buyone.orderservice.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);           // Buyer orders
    List<Order> findByUserIdAndStatus(String userId, OrderStatus status);
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // Add these methods
    @Query(value = "{ 'userId': ?0, $or: [ " +
            "{ 'orderNumber': { $regex: ?1, $options: 'i' } }, " +
            "{ 'items.productName': { $regex: ?1, $options: 'i' } }, " +
            "{ 'status': ?2 } ] }",
            sort = "{ 'createdAt': -1 }")
    Page<Order> findBuyerOrdersSearch(String userId, String keyword, OrderStatus status, Pageable pageable);
    
    @Query("{ 'items.sellerId': ?0 }")
    Page<Order> findSellerOrders(String sellerId, Pageable pageable);
}
