package com.buyone.orderservice.service;

import com.buyone.orderservice.request.CreateProductRequest;
import com.buyone.orderservice.request.UpdateProductRequest;
import com.buyone.orderservice.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request, String sellerId);
    ProductResponse getProductById(String id);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(String id, UpdateProductRequest request, String sellerId);
    void deleteProduct(String id, String sellerId);
    List<ProductResponse> getProductsBySeller(String sellerId); // for seller dashboard
}


// Methods should use DTOs for incoming and outgoing data (except for internal lookups).