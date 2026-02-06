package com.buyone.orderservice.client;

import com.buyone.orderservice.dto.request.ReserveStockRequest;
import com.buyone.orderservice.dto.request.ReleaseStockRequest;
import com.buyone.orderservice.dto.response.ProductResponse;
import com.buyone.orderservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/products/{id}")
    ApiResponse<ProductResponse> getById(@PathVariable("id") String id);
    
    @PostMapping("/products/stock/reserve")
    ApiResponse<Void> reserveStock(@RequestBody ReserveStockRequest request);
    
    @PostMapping("/products/stock/release")
    ApiResponse<Void> releaseStock(@RequestBody ReleaseStockRequest request);
    
    @PostMapping("products/stock/commit/{orderNumber}")
    ApiResponse<Void> commitStock(@PathVariable("orderNumber") String orderNumber);
}
