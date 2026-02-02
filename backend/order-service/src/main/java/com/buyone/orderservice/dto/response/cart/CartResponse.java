// CartResponse.java
package com.buyone.orderservice.dto.response.cart;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class CartResponse {
    String id;
    String userId;
    List<CartItemResponse> items;
    BigDecimal subtotal;
    BigDecimal tax;
    BigDecimal total;
}
