package com.buyone.orderservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Cart {
    @Id
    private String id;
    private String userId;
    
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    private double subtotal;
    private double tax;
    private double total;
    private LocalDateTime updatedAt;
}
