package com.buyone.orderservice.service;

import com.buyone.orderservice.dto.response.analytics.ClientAnalyticsResponse;
import com.buyone.orderservice.dto.response.analytics.SellerAnalyticsResponse;

public interface ProfileAnalyticsService {
    ClientAnalyticsResponse getClientAnalytics(String userId);
    SellerAnalyticsResponse getSellerAnalytics(String sellerId);
}
