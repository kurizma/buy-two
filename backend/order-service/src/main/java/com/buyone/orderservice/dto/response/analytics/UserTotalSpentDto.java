package com.buyone.orderservice.dto.response.analytics;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

@Data @Builder
public class UserTotalSpentDto {
    private String _id;      // userId
    private BigDecimal totalSpent;
}
