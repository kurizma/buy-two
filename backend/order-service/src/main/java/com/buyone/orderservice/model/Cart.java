package com.buyone.orderservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    private String id; // userId
    
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    
    private double subtotal;
    private double tax;
    private double total;
    
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
}
