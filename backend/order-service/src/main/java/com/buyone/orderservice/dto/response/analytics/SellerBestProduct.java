package com.buyone.orderservice.dto.response.analytics;

import java.math.BigDecimal;

public record SellerBestProduct(String productId, String name, BigDecimal revenue, Integer unitsSold, String category) {
}
