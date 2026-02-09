package com.buyone.orderservice.dto.response.analytics;

import java.math.BigDecimal;

public record SellerTopCategory(String category, BigDecimal totalRevenue) {}
