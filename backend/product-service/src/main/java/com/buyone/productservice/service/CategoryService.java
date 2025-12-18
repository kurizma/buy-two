package com.buyone.productservice.service;

import com.buyone.productservice.request.UpdateCategoryRequest;
import com.buyone.productservice.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(String id);
    CategoryResponse updateCategory(String id, UpdateCategoryRequest request);
    void deleteCategory(String id);
}
