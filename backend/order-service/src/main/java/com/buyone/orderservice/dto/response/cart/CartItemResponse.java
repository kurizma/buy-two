// CartItemResponse.java
package com.buyone.orderservice.dto.response.cart;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

@Data @Builder
public class CartItemResponse {
    String productId;
    String productName;
    String imageUrl;
    String sellerId;
    BigDecimal price;
    int quantity;
    BigDecimal lineTotal;
}
