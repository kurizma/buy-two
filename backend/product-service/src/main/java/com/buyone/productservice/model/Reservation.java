package com.buyone.productservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@Document("reservations")
public class Reservation {
    @Id private String id;
    private String productId;
    private int quantity;
    private String orderNumber;
    
    @Indexed(expireAfterSeconds = 60)  // 1 min auto-delete; uses seconds
    private LocalDateTime createdAt;
}
