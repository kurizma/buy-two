package com.buyone.orderservice.dto.response.analytics;

import java.math.BigDecimal;

public record ClientMostBought(String productId, String name, Integer totalQty, BigDecimal totalAmount) {}
