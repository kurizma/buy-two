package com.buyone.productservice.repository;

import com.buyone.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByUserId(String userId);
    void deleteByUserId(String userId);
}
