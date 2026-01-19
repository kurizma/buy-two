package com.buyone.orderservice.service;

import com.buyone.orderservice.request.UpdateCategoryRequest;
import com.buyone.orderservice.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(String id);
    CategoryResponse updateCategory(String id, UpdateCategoryRequest request);
    void deleteCategory(String id);
}
