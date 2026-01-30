package com.buyone.orderservice.client;

import com.buyone.orderservice.dto.response.ProductResponse;
import com.buyone.orderservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/products/{id}")
    ApiResponse<ProductResponse> getById(@PathVariable("id") String id);
}
