package com.buyone.orderservice.client;

import com.buyone.orderservice.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")  // Calls http://product-service:8567
public interface ProductClient {
    @GetMapping("/api/products/{id}")  // GET /api/products/PROD123
    Product getById(@PathVariable("id") String id);
}
