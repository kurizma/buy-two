package com.buyone.orderservice.model.cart;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Setter;

import java.math.BigDecimal;
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
    @Indexed(unique = true)
    private String id;
    private String userId;
    
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal total;
    private LocalDateTime updatedAt;
}
