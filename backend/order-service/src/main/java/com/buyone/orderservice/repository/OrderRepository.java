package com.buyone.orderservice.repository;

import com.buyone.orderservice.dto.response.analytics.*;
import com.buyone.orderservice.model.order.Order;
import com.buyone.orderservice.model.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.buyone.orderservice.dto.response.analytics.ClientTotalSpent;
import com.buyone.orderservice.dto.response.analytics.ClientMostBought;
import com.buyone.orderservice.dto.response.analytics.ClientTopCategory;
import com.buyone.orderservice.dto.response.analytics.SellerTotalRevenue;
import com.buyone.orderservice.dto.response.analytics.SellerBestProduct;
import com.buyone.orderservice.dto.response.analytics.SellerTotalUnits;


import java.util.List;
import java.util.Optional;

/**
 * OrderRepository with analytics aggregations for user/seller dashboards.
 * Uses MongoDB aggregation pipelines for efficient, indexed analytics queries.
 * Indexes recommended: {userId:1, status:1}, {status:1, "items.sellerId":1}
 */
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
    // === CLIENT ANALYTICS ===
    @Aggregation(value = {
            "{ $match: { userId: ?0, status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
            "{ $group: { _id: '$userId', totalSpent: { $sum: '$total' } } }"
    })
    List<ClientTotalSpent> getClientTotalSpent(String userId);
    
    @Aggregation(value = {
            "{ $match: { userId: ?0, status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
            "{ $unwind: '$items' }",
            "{ $group: { _id: { productId: '$items.productId', name: '$items.productName' }, totalQty: { $sum: '$items.quantity' } } }",
            "{ $sort: { totalQty: -1 } }",
            "{ $limit: 5 }"
    })
    List<ClientMostBought> getClientMostBought(String userId);
    
    @Aggregation(value = {
            "{ $match: { userId: ?0, status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
            "{ $unwind: '$items' }",
            "{ $group: { _id: '$items.category', totalSpent: { $sum: { $multiply: [ '$items.price', '$items.quantity' ] } } } }",
            "{ $sort: { totalSpent: -1 } }",
            "{ $limit: 5 }"
    })
    List<ClientTopCategory> getClientTopCategories(String userId);
    
    // === SELLER ANALYTICS ===
    @Aggregation(value = {
            "{ $match: { status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
            "{ $unwind: '$items' }",
            "{ $match: { 'items.sellerId': ?0 } }",
            "{ $group: { _id: null, totalRevenue: { $sum: { $multiply: [ '$items.price', '$items.quantity' ] } } } }"
    })
    List<SellerTotalRevenue> getSellerTotalRevenue(String sellerId);
    
    @Aggregation(value = {
            "{ $match: { status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
            "{ $unwind: '$items' }",
            "{ $match: { 'items.sellerId': ?0 } }",
            "{ $group: { _id: { productId: '$items.productId', name: '$items.productName' }, revenue: { $sum: { $multiply: [ '$items.price', '$items.quantity' ] } }, unitsSold: { $sum: '$items.quantity' } } }",
            "{ $sort: { revenue: -1 } }",
            "{ $limit: 5 }"
    })
    List<SellerBestProduct> getSellerBestProducts(String sellerId);
    
    @Aggregation(value = {
            "{ $match: { status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
            "{ $unwind: '$items' }",
            "{ $match: { 'items.sellerId': ?0 } }",
            "{ $group: { _id: null, totalUnits: { $sum: '$items.quantity' } } }"
    })
    List<SellerTotalUnits> getSellerTotalUnits(String sellerId);
    
}
