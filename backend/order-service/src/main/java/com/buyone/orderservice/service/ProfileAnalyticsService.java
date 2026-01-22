import com.buyone.orderservice.dto.response.analytics.TopProductDto;
import com.buyone.orderservice.dto.response.analytics.UserTotalSpentDto;
import com.buyone.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileAnalyticsService {
    private final MongoTemplate mongoTemplate;
    private final OrderRepository orderRepository;  // For simple totals
    
    public UserAnalyticsResponse getClientAnalytics(String userId) {
        // Total spent (simple @Aggregation in repo)
        BigDecimal totalSpent = orderRepository.getUserTotalSpent(userId)
                .stream().findFirst()
                .map(UserTotalSpentDto::getTotalSpent)
                .orElse(BigDecimal.ZERO);
        
        // Top products (complex pipeline)
        Aggregation agg = Aggregation.newAggregation(
                match(Criteria.where("userId").is(userId)
                        .and("status").in(completedStatuses())),
                unwind("items"),
                group("items.productId")
                        .first("items.productName").as("productName")
                        .sum("items.quantity").as("unitsBought")
                        .sum(multiply("items.price", "items.quantity")).as("amountSpent"),
                sort(Sort.by(Sort.Direction.DESC, "unitsBought")),
                limit(5)
        );
        
        List<TopProductDto> topProducts = mongoTemplate.aggregate(agg, "orders", TopProductDto.class)
                .getMappedResults();
        
        return UserAnalyticsResponse.builder()
                .totalSpent(totalSpent)
                .topProducts(topProducts)
                .build();
    }
    
    public SellerAnalyticsResponse getSellerAnalytics(String sellerId) {
        Aggregation agg = Aggregation.newAggregation(
                match(Criteria.where("status").in(completedStatuses())),
                unwind("items"),
                match(Criteria.where("items.sellerId").is(sellerId)),
                group("items.productId")
                        .first("items.productName").as("productName")
                        .sum("items.quantity").as("unitsSold")
                        .sum(multiply("items.price", "items.quantity")).as("revenue"),
                sort(Sort.by(Sort.Direction.DESC, "revenue")),
                limit(5)
        );
        
        List<TopProductDto> bestSellers = mongoTemplate.aggregate(agg, "orders", TopProductDto.class)
                .getMappedResults();
        
        BigDecimal totalRevenue = calculateTotalRevenue(sellerId);  // Repo method
        
        return SellerAnalyticsResponse.builder()
                .totalRevenue(totalRevenue)
                .bestSellers(bestSellers)
                .build();
    }
    
    private List<OrderStatus> completedStatuses() {
        return List.of(OrderStatus.DELIVERED, OrderStatus.CONFIRMED);
    }
}
