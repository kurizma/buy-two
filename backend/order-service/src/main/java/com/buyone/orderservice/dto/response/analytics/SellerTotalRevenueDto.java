package com.buyone.orderservice.dto.response.analytics;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

@Data @Builder
public class SellerTotalRevenueDto {
    private String _id;        // null
    private BigDecimal totalRevenue;
}
