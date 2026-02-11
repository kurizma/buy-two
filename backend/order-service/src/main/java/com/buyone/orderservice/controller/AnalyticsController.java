package com.buyone.orderservice.controller;

import com.buyone.orderservice.dto.response.ApiResponse;
import com.buyone.orderservice.dto.response.analytics.ClientAnalyticsResponse;
import com.buyone.orderservice.dto.response.analytics.SellerAnalyticsResponse;
import com.buyone.orderservice.service.ProfileAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final ProfileAnalyticsService profileAnalyticsService;
    
    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<ClientAnalyticsResponse>> getClientAnalytics(
            @PathVariable String clientId) {
        ClientAnalyticsResponse analytics = profileAnalyticsService.getClientAnalytics(clientId);
        return ResponseEntity.ok(ApiResponse.<ClientAnalyticsResponse>builder()
                .success(true)
                .message("Client analytics fetched successfully")
                .data(analytics)
                .build());
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<ApiResponse<SellerAnalyticsResponse>> getSellerAnalytics(
            @PathVariable String sellerId) {
        SellerAnalyticsResponse analytics = profileAnalyticsService.getSellerAnalytics(sellerId);
        return ResponseEntity.ok(ApiResponse.<SellerAnalyticsResponse>builder()
                .success(true)
                .message("Seller analytics fetched successfully")
                .data(analytics)
                .build());
    }
}
