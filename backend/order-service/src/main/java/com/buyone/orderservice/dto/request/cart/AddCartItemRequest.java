// AddCartItemRequest.java
package com.buyone.orderservice.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;

@Data @Builder
public class AddCartItemRequest {
    @NotBlank String productId;
    @NotBlank String sellerId;
    @Min(1) int quantity;
}
