package com.buyone.orderservice.dto.request.order;

import com.buyone.orderservice.model.Address;
import jakarta.validation.Valid;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @Valid
    private Address shippingAddress;
}
