package com.buyone.orderservice.repository;

import com.buyone.orderservice.dto.response.analytics.SellerTotalRevenueDto;
import com.buyone.orderservice.dto.response.analytics.UserTotalSpentDto;
import com.buyone.orderservice.model.order.Order;
import com.buyone.orderservice.model.order.OrderStatus;
import org.springframework.data.mongodb.repository.Aggregation;
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
    
    @Query(value = "{ 'userId': ?0, $or: [ " +
            "{ 'orderNumber': { $regex: ?1, $options: 'i' } }, " +
            "{ 'items.productName': { $regex: ?1, $options: 'i' } }, " +
            "{ 'status': ?2 } ] }",
            sort = "{ 'createdAt': -1 }")
    Page<Order> findBuyerOrdersSearch(String userId, String keyword, OrderStatus status, Pageable pageable);
    
    @Query("{ 'items.sellerId': ?0 }")
    Page<Order> findSellerOrders(String sellerId, Pageable pageable);
    
    // Simple total spent for CLIENT (pre-filtered by userId)
    @Aggregation(value = {
            "{ $match: { userId: ?0, status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
            "{ $group: { _id: '$userId', totalSpent: { $sum: '$total' } } }"
    })
    List<UserTotalSpentDto> getUserTotalSpent(String userId);
    
    // Total revenue for SELLER (unwind + sum across their items)
    @Aggregation(value = {
            "{ $match: { status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
            "{ $unwind: '$items' }",
            "{ $match: { 'items.sellerId': ?0 } }",
            "{ $group: { _id: null, totalRevenue: { $sum: { $multiply: [ '$items.price', '$items.quantity' ] } } } }"
    })
    List<SellerTotalRevenueDto> getSellerTotalRevenue(String sellerId);
}
