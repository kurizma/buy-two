// src/main/java/com/buyone/productservice/request/UpdateCategoryRequest.java
package com.buyone.productservice.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
    
    private String icon;
    private String description;
}
