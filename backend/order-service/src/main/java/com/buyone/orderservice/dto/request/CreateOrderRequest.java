package com.buyone.orderservice.dto.request;

import com.buyone.orderservice.model.Address;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CreateOrderRequest {
    @Valid
    private Address shippingAddress;
}
