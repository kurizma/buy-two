package com.buyone.orderservice.model;

import lombok.*;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;
    
    private String userId;       // Buyer
    private String orderNumber;  // Unique: "ORD-" + UUID
    
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    private String status;       // "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"
    private String paymentMethod = "PAY_ON_DELIVERY";
    
    private RabbitConnectionDetails.Address shippingAddress;  // Inline or ref
    private double subtotal;
    private double tax;
    private double total;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
