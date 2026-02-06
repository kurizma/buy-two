// UpdateCartQuantityRequest.java
package com.buyone.orderservice.dto.request.cart;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateCartQuantityRequest {
    @Min(1) int quantity;
}
