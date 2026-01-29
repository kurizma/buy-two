package com.buyone.orderservice.model.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @NotBlank
    private String productId;
    
    @NotBlank
    private String sellerId;
    
    @NotBlank
    private String productName;
    
    private BigDecimal price;
    
    @Min(1)
    private int quantity = 1;
    
    private String imageUrl;
}
