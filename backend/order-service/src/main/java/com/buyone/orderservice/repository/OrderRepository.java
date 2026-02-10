package com.buyone.orderservice.repository;

import com.buyone.orderservice.dto.response.analytics.*;
import com.buyone.orderservice.model.order.Order;
import com.buyone.orderservice.model.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * OrderRepository with analytics aggregations for user/seller dashboards.
 * Uses MongoDB aggregation pipelines for efficient, indexed analytics queries.
 * Indexes recommended: {userId:1, status:1}, {status:1, "items.sellerId":1}
 */
public interface OrderRepository extends MongoRepository<Order, String> {

        List<Order> findByUserId(String userId); // Buyer orders

        List<Order> findByUserIdAndStatus(String userId, OrderStatus status);

        Optional<Order> findByOrderNumber(String orderNumber);

        @Query(value = "{ 'userId': ?0, $or: [ " +
                        "{ 'orderNumber': { $regex: ?1, $options: 'i' } }, " +
                        "{ 'items.productName': { $regex: ?1, $options: 'i' } }, " +
                        "{ 'status': ?2 } ] }", sort = "{ 'createdAt': -1 }")
        Page<Order> findBuyerOrdersSearch(String userId, String keyword, OrderStatus status, Pageable pageable);

        @Query("{ 'items.sellerId': ?0 }")
        Page<Order> findSellerOrders(String sellerId, Pageable pageable);

        // === CLIENT ANALYTICS ===
        @Aggregation(value = {
                        "{ $match: { userId: ?0, status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
                        "{ $group: { _id: null, totalSpent: { $sum: { $toDouble: '$total' } } } }",
                        "{ $project: { _id: 0, totalSpent: 1 } }"
        })
        List<ClientTotalSpent> getClientTotalSpent(String userId);

        @Aggregation(value = {
                        "{ $match: { userId: ?0, status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
                        "{ $unwind: '$items' }",
                        "{ $addFields: { 'items.productObjectId': { $toObjectId: '$items.productId' } } }",
                        // ⭐ 1st lookup: Get product
                        "{ $lookup: { from: 'products', localField: 'items.productObjectId', foreignField: '_id', as: 'productDetails' } }",
                        "{ $unwind: { path: '$productDetails', preserveNullAndEmptyArrays: true } }",
                        // ⭐ 2nd lookup: Get category by product.categoryId
                        "{ $lookup: { from: 'categories', localField: 'productDetails.categoryId', foreignField: '_id', as: 'categoryDetails' } }",
                        "{ $unwind: { path: '$categoryDetails', preserveNullAndEmptyArrays: true } }",
                        "{ $group: { " +
                                        "  _id: { productId: '$items.productId', name: '$items.productName', category: { $ifNull: ['$categoryDetails.name', 'Uncategorized'] } }, "
                                        +
                                        "  totalQty: { $sum: '$items.quantity' }, " +
                                        "  totalAmount: { $sum: { $multiply: [ { $toDouble: '$items.price' }, { $toDouble: '$items.quantity' } ] } } "
                                        +
                                        "} }",
                        "{ $project: { " +
                                        "  _id: 0, " +
                                        "  productId: '$_id.productId', " +
                                        "  name: '$_id.name', " +
                                        "  category: '$_id.category', " +
                                        "  totalQty: 1, " +
                                        "  totalAmount: 1 " +
                                        "} }",
                        "{ $sort: { totalQty: -1 } }",
                        "{ $limit: 5 }"
        })
        List<ClientMostBought> getClientMostBought(String userId);
        
        @Aggregation(value = {
                        "{ $match: { userId: ?0, status: 'CONFIRMED' } }",
                        "{ $unwind: '$items' }",
                        "{ $addFields: { 'items.productObjectId': { $toObjectId: '$items.productId' } } }",
                        "{ $lookup: { " +
                                        "  from: 'products', " +
                                        "  localField: 'items.productObjectId', " +
                                        "  foreignField: '_id', " +
                                        "  as: 'productDetails' " +
                                        "} }",
                        "{ $unwind: { path: '$productDetails', preserveNullAndEmptyArrays: true } }",
                        "{ $lookup: { " +
                                        "  from: 'categories', " + // ✅ Correct collection name
                                        "  localField: 'productDetails.categoryId', " + // ✅ Matches Product.categoryId
                                        "  foreignField: '_id', " + // ✅ Matches Category._id ("CAT-002")
                                        "  as: 'categoryDetails' " +
                                        "} }",
                        "{ $unwind: { path: '$categoryDetails', preserveNullAndEmptyArrays: true } }",
                        "{ $group: { " +
                                        "  _id: '$categoryDetails.name', " + // ✅ Uses Category.name ("Anime & Pop
                                                                             // Culture")
                                        "  totalSpent: { $sum: { $multiply: [ { $toDouble: '$items.price' }, '$items.quantity' ] } } "
                                        +
                                        "} }",
                        "{ $match: { _id: { $ne: null } } }",
                        "{ $project: { _id: 0, category: '$_id', totalSpent: 1 } }",
                        "{ $sort: { totalSpent: -1 } }",
                        "{ $limit: 5 }"
        })
        List<ClientTopCategory> getClientTopCategories(String userId);

        // === SELLER ANALYTICS ===
        @Aggregation(value = {
                        "{ $match: { status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
                        "{ $unwind: '$items' }",
                        "{ $match: { 'items.sellerId': ?0 } }",
                        "{ $group: { _id: null, totalRevenue: { $sum: { $multiply: [ { $toDouble: '$items.price' },  '$items.quantity'  ] } } } }"
        })
        List<SellerTotalRevenue> getSellerTotalRevenue(String sellerId);

        @Aggregation(value = {
                        "{ $match: { status: 'CONFIRMED' } }",
                        "{ $unwind: '$items' }",
                        "{ $match: { 'items.sellerId': ?0 } }",
                        "{ $addFields: { 'items.productObjectId': { $toObjectId: '$items.productId' } } }",
                        "{ $lookup: { from: 'products', localField: 'items.productObjectId', foreignField: '_id', as: 'productDetails' } }",
                        "{ $unwind: { path: '$productDetails', preserveNullAndEmptyArrays: true } }",
                        "{ $lookup: { from: 'categories', localField: 'productDetails.categoryId', foreignField: '_id', as: 'categoryDetails' } }",
                        "{ $unwind: { path: '$categoryDetails', preserveNullAndEmptyArrays: true } }",
                        "{ $group: { " +
                                        "  _id: { productId: '$items.productId', name: '$items.productName', category: { $ifNull: ['$categoryDetails.name', 'Uncategorized'] } }, "
                                        +
                                        "  revenue: { $sum: { $multiply: [ { $toDouble: '$items.price' }, { $toDouble: '$items.quantity' } ] } }, "
                                        +
                                        "  unitsSold: { $sum: '$items.quantity' } " +
                                        "} }",
                        "{ $project: { " +
                                        "  _id: 0, productId: '$_id.productId', name: '$_id.name', category: '$_id.category', revenue: 1, unitsSold: 1 "
                                        +
                                        "} }",
                        "{ $sort: { revenue: -1 } }",
                        "{ $limit: 5 }"
        })
        List<SellerBestProduct> getSellerBestProducts(String sellerId);

        @Aggregation(value = {
                        "{ $match: { status: 'CONFIRMED' } }",
                        "{ $unwind: '$items' }",
                        "{ $addFields: { 'items.productObjectId': { $toObjectId: '$items.productId' } } }",
                        "{ $match: { 'items.sellerId': ?0 } }",
                        "{ $lookup: { " +
                                        "  from: 'products', " +
                                        "  localField: 'items.productObjectId', " +
                                        "  foreignField: '_id', " +
                                        "  as: 'productDetails' " +
                                        "} }",
                        "{ $unwind: { path: '$productDetails', preserveNullAndEmptyArrays: true } }",
                        "{ $lookup: { " +
                                        "  from: 'categories', " +
                                        "  localField: 'productDetails.categoryId', " +
                                        "  foreignField: '_id', " +
                                        "  as: 'categoryDetails' " +
                                        "} }",
                        "{ $unwind: { path: '$categoryDetails', preserveNullAndEmptyArrays: true } }",
                        "{ $group: { " +
                                        "  _id: '$categoryDetails.name', " +
                                        "  totalRevenue: { $sum: { $multiply: [ { $toDouble: '$items.price' }, '$items.quantity' ] } } "
                                        +
                                        "} }",
                        "{ $match: { _id: { $ne: null } } }",
                        "{ $project: { _id: 0, category: '$_id', totalRevenue: 1 } }",
                        "{ $sort: { totalRevenue: -1 } }",
                        "{ $limit: 5 }"
        })
        List<SellerTopCategory> getSellerTopCategories(String sellerId);

        @Aggregation(value = {
                        "{ $match: { status: { $in: ['DELIVERED', 'CONFIRMED'] } } }",
                        "{ $unwind: '$items' }",
                        "{ $match: { 'items.sellerId': ?0 } }",
                        "{ $group: { _id: null, totalUnits: { $sum: { $toDouble: '$items.quantity' } } } }"
        })
        List<SellerTotalUnits> getSellerTotalUnits(String sellerId);

}
