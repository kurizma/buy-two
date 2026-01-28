package com.buyone.orderservice.dto.response.analytics;

import java.math.BigDecimal;
import java.util.List;

public record ClientAnalyticsResponse(
        BigDecimal totalSpent,
        List<ClientMostBought> mostBoughtProducts,
        List<ClientTopCategory> topCategories
) {}
