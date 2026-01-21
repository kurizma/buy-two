package com.buyone.orderservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.buyone.orderservice.model.OrderStatus.PENDING;
import static com.buyone.orderservice.model.PaymentMethod.PAY_ON_DELIVERY;

@Document(collection = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;
    
    @Indexed
    private String userId;       // "Buyer" - CLIENT Role Only
    
    @Indexed(unique = true)
    private String orderNumber;  // Unique: "ORD-" + UUID
    
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    @Builder.Default
    private OrderStatus status = PENDING;
    // "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"
    
    @Builder.Default
    private PaymentMethod paymentMethod = PAY_ON_DELIVERY;
    
    private Address shippingAddress;  // Inline or ref
    
    @Builder.Default
    private BigDecimal subtotal;
    @Builder.Default
    private BigDecimal tax;
    @Builder.Default
    private BigDecimal total;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
