package com.buyone.orderservice.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String productId;
    private String productName;
    private String sellerId;
    private double price;
    private int quantity;
    private String imageUrl;
}
