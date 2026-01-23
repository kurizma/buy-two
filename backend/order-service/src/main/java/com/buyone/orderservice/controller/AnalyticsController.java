package com.buyone.orderservice.controller;

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
    
    @GetMapping("/client/{userId}")
    public ResponseEntity<ClientAnalyticsResponse> getClientAnalytics(
            @PathVariable String userId) {
        return ResponseEntity.ok(profileAnalyticsService.getClientAnalytics(userId));
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<SellerAnalyticsResponse> getSellerAnalytics(
            @PathVariable String sellerId) {
        return ResponseEntity.ok(profileAnalyticsService.getSellerAnalytics(sellerId));
    }
}
