package com.buyone.orderservice.dto.response.analytics;

import java.math.BigDecimal;

public record ClientTopCategory(String category, BigDecimal totalSpent) {}
