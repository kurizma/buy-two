package com.buyone.productservice.service;

import com.buyone.productservice.request.CreateProductRequest;
import com.buyone.productservice.request.UpdateProductRequest;
import com.buyone.productservice.response.ProductResponse;

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