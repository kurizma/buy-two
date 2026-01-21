package com.buyone.orderservice.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String productId;
    private String productName;
    private String sellerId;    // For seller dashboards/revenue "SELLER" from user DB
    private BigDecimal price;
    private int quantity;
    private String imageUrl;    // First image from Product.images
}
