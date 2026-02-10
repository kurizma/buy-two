package com.buyone.orderservice.controller;

import com.buyone.orderservice.config.SecurityConfig;
import com.buyone.orderservice.dto.response.analytics.*;
import com.buyone.orderservice.exception.GlobalExceptionHandler;
import com.buyone.orderservice.service.ProfileAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AnalyticsControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProfileAnalyticsService profileAnalyticsService;
    
    @Test
    void getClientAnalytics_returns200_withData() throws Exception {
        ClientAnalyticsResponse analytics = new ClientAnalyticsResponse(
                BigDecimal.valueOf(500),
                List.of(new ClientMostBought("p1", "Widget", 10)),
                List.of(new ClientTopCategory("Electronics", BigDecimal.valueOf(300)))
        );
        when(profileAnalyticsService.getClientAnalytics("client-1")).thenReturn(analytics);
        
        mockMvc.perform(get("/api/analytics/client/client-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSpent").value(500))
                .andExpect(jsonPath("$.data.mostBoughtProducts[0].name").value("Widget"));
    }
    
    @Test
    void getSellerAnalytics_returns200_withData() throws Exception {
        SellerAnalyticsResponse analytics = new SellerAnalyticsResponse(
                BigDecimal.valueOf(1000),
                List.of(new SellerBestProduct("p1", "Widget", BigDecimal.valueOf(500), 50, "Electronics")),
                100,
                List.of(new SellerTopCategory("Electronics", BigDecimal.valueOf(1000)))
        );
        when(profileAnalyticsService.getSellerAnalytics("seller-1")).thenReturn(analytics);
        
        mockMvc.perform(get("/api/analytics/seller/seller-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalRevenue").value(1000))
                .andExpect(jsonPath("$.data.totalUnitsSold").value(100));
    }
}
