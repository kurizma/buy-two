package com.buyone.productservice.repository;

import com.buyone.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByUserId(String userId);
    void deleteByUserId(String userId);
    
    @Query("{ $and: [ " +
           "{ $or: [ " +
           "  { 'name': { $regex: ?0, $options: 'i' } }, " +
           "  { 'description': { $regex: ?0, $options: 'i' } }" +
           "] }, " +
           "{ 'price': { $gte: ?1, $lte: ?2 } }, " +
           "{ $or: [ " +
           "  { 'categoryId': ?3 }, " +
           "  { $expr: { $eq: [?3, null] } } " +
           "] } " +
           "] }")
    Page<Product> findByFacetedSearch(
        String keyword, 
        BigDecimal minPrice, 
        BigDecimal maxPrice, 
        String categoryId, 
        Pageable pageable
    );
}
