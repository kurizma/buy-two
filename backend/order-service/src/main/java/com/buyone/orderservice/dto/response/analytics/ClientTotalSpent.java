package com.buyone.orderservice.dto.response.analytics;

import java.math.BigDecimal;

public record ClientTotalSpent(String userId, BigDecimal totalSpent) {}
