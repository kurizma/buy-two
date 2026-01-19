package com.buyone.orderservice.repository;

import com.buyone.orderservice.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepository extends MongoRepository<Category, String> {
}
