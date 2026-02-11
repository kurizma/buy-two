package com.buyone.orderservice.dto.response.analytics;

public record ClientMostBought(String productId, String name, Integer totalQty, BigDecimal totalAmount,
        String category) {
}
