package com.buyone.productservice.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReserveStockRequest {
    @NotBlank(message = "Product ID is required")
    private String productId;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
    
    @NotBlank(message = "Order number is required")
    private String orderNumber;
}
