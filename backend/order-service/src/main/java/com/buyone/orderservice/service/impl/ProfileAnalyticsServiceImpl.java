package com.buyone.orderservice.service.impl;

import com.buyone.orderservice.dto.response.analytics.*;
import com.buyone.orderservice.repository.OrderRepository;
import com.buyone.orderservice.service.ProfileAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileAnalyticsServiceImpl implements ProfileAnalyticsService {
    
    private final OrderRepository orderRepository;
    
    @Override
    public ClientAnalyticsResponse getClientAnalytics(String userId) {
        // ✅ Use your new repo methods (no MongoTemplate needed!)
        List<ClientTotalSpent> totalSpentList = orderRepository.getClientTotalSpent(userId);
        List<ClientMostBought> mostBought = orderRepository.getClientMostBought(userId);
        List<ClientTopCategory> topCategories = orderRepository.getClientTopCategories(userId);
        
        BigDecimal totalSpent = totalSpentList.isEmpty()
                ? BigDecimal.ZERO
                : totalSpentList.get(0).totalSpent();
        
        log.info("Client {} analytics: ${} spent, {} top products",
                userId, totalSpent, mostBought.size());
        
        return new ClientAnalyticsResponse(totalSpent, mostBought, topCategories);
    }
    
    @Override
    public SellerAnalyticsResponse getSellerAnalytics(String sellerId) {
        List<SellerTotalRevenue> revenueList = orderRepository.getSellerTotalRevenue(sellerId);
        List<SellerBestProduct> bestProducts = orderRepository.getSellerBestProducts(sellerId);
        List<SellerTotalUnits> unitsList = orderRepository.getSellerTotalUnits(sellerId);
        List<SellerTopCategory> topCategories = orderRepository.getSellerTopCategories(sellerId);
        
        BigDecimal totalRevenue = revenueList.isEmpty()
                ? BigDecimal.ZERO
                : revenueList.get(0).totalRevenue();
        
        Integer totalUnits = unitsList.isEmpty()
                ? 0
                : unitsList.get(0).totalUnits();
        
        log.info("Seller {} analytics: ${} revenue, {} units, {} top products, {} top categories",
                sellerId, totalRevenue, totalUnits, bestProducts.size(), topCategories.size());
        
        return new SellerAnalyticsResponse(totalRevenue, bestProducts, totalUnits, topCategories);
    }
}
