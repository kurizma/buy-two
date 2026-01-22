package com.buyone.orderservice.dto.response.analytics;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TopProductDto {
    private String productId;
    private String productName;
    private int unitsBought;
    private BigDecimal amountSpent;
}
