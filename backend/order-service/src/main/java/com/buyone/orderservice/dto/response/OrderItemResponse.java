package com.buyone.orderservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private String productName;
    private String sellerId;
    private BigDecimal price;
    private int quantity;
}
