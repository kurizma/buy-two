package com.buyone.productservice.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {
    
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
    private BigDecimal price;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private String categoryId;

    private java.util.List<String> images;
}
