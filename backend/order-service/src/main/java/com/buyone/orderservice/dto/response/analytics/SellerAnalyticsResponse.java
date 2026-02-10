package com.buyone.orderservice.dto.response.analytics;

import java.math.BigDecimal;
import java.util.List;

public record SellerAnalyticsResponse(
        BigDecimal totalRevenue,
        List<SellerBestProduct> bestSellingProducts,
        Integer totalUnitsSold,
        List<SellerTopCategory> topCategories
) {}
