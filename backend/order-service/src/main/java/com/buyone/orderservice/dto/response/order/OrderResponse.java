package com.buyone.orderservice.dto.response.order;

import com.buyone.orderservice.model.Address;
import com.buyone.orderservice.model.order.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal total, subtotal, tax, shippingCost;
    private LocalDateTime createdAt;
    private Address shippingAddress;
    private List<OrderItemResponse> items;
}
