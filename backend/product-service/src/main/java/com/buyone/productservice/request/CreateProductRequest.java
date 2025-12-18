package com.buyone.productservice.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.*;

/**
 * DTO for product creation, validates all required/optional fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
    private Double price;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be zero or greater")
    private Integer quantity;
    
    private String categoryId;
    
    private java.util.List<String> images;

    // userId is NOT included here, it comes from JWT / header in the controller for security!
    // Add more fields as model extends, with corresponding validations
    
    // For example, for media/image in future:
    // private String mediaId;
}
