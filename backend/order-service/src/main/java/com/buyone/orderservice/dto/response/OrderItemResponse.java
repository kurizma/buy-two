package com.buyone.orderservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private String productName;
    private String sellerId;
    private double price;
    private int quantity;
}
