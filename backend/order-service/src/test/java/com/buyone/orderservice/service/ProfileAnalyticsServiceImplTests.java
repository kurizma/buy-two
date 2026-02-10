package com.buyone.orderservice.service;

import com.buyone.orderservice.dto.response.analytics.*;
import com.buyone.orderservice.repository.OrderRepository;
import com.buyone.orderservice.service.impl.ProfileAnalyticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileAnalyticsServiceImplTests {
    
    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private ProfileAnalyticsServiceImpl service;
    
    // ========== CLIENT ANALYTICS ==========
    
    @Test
    void getClientAnalytics_returnsFullData() {
        when(orderRepository.getClientTotalSpent("user-1"))
                .thenReturn(List.of(new ClientTotalSpent("user-1", BigDecimal.valueOf(500))));
        when(orderRepository.getClientMostBought("user-1"))
                .thenReturn(List.of(new ClientMostBought("p1", "Widget", 10)));
        when(orderRepository.getClientTopCategories("user-1"))
                .thenReturn(List.of(new ClientTopCategory("Electronics", BigDecimal.valueOf(300))));
        
        ClientAnalyticsResponse result = service.getClientAnalytics("user-1");
        
        assertThat(result.totalSpent()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(result.mostBoughtProducts()).hasSize(1);
        assertThat(result.topCategories()).hasSize(1);
    }
    
    @Test
    void getClientAnalytics_returnsZero_whenNoOrders() {
        when(orderRepository.getClientTotalSpent("user-empty"))
                .thenReturn(List.of());
        when(orderRepository.getClientMostBought("user-empty"))
                .thenReturn(List.of());
        when(orderRepository.getClientTopCategories("user-empty"))
                .thenReturn(List.of());
        
        ClientAnalyticsResponse result = service.getClientAnalytics("user-empty");
        
        assertThat(result.totalSpent()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.mostBoughtProducts()).isEmpty();
        assertThat(result.topCategories()).isEmpty();
    }
    
    // ========== SELLER ANALYTICS ==========
    
    @Test
    void getSellerAnalytics_returnsFullData() {
        when(orderRepository.getSellerTotalRevenue("seller-1"))
                .thenReturn(List.of(new SellerTotalRevenue(BigDecimal.valueOf(1000))));
        when(orderRepository.getSellerBestProducts("seller-1"))
                .thenReturn(List.of(new SellerBestProduct("p1", "Widget", BigDecimal.valueOf(500), 50, "Electronics")));
        when(orderRepository.getSellerTotalUnits("seller-1"))
                .thenReturn(List.of(new SellerTotalUnits(100)));
        
        SellerAnalyticsResponse result = service.getSellerAnalytics("seller-1");
        
        assertThat(result.totalRevenue()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(result.bestSellingProducts()).hasSize(1);
        assertThat(result.totalUnitsSold()).isEqualTo(100);
    }
    
    @Test
    void getSellerAnalytics_returnsDefaults_whenNoData() {
        when(orderRepository.getSellerTotalRevenue("seller-empty"))
                .thenReturn(List.of());
        when(orderRepository.getSellerBestProducts("seller-empty"))
                .thenReturn(List.of());
        when(orderRepository.getSellerTotalUnits("seller-empty"))
                .thenReturn(List.of());
        
        SellerAnalyticsResponse result = service.getSellerAnalytics("seller-empty");
        
        assertThat(result.totalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.bestSellingProducts()).isEmpty();
        assertThat(result.totalUnitsSold()).isEqualTo(0);
    }
}
