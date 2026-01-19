package com.buyone.orderservice.repository;

import com.buyone.orderservice.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findById(String userId);
    
    
    // MongoRepository ALREADY has these:
    // Cart save(Cart cart);           ← inherited
    // void deleteById(String userId); ← inherited
    
    // CUSTOM queries we'll need:
    // void deleteItem(String userId, String productId);  (later)
}
