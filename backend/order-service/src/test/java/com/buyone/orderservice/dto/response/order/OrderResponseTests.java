package com.buyone.orderservice.dto.response.order;

import com.buyone.orderservice.model.Address;
import com.buyone.orderservice.model.order.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderResponseTests {

    @Test
    void testOrderResponseBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Address address = Address.builder()
                .street("123 Main St")
                .city("New York")
                .build();

        OrderItemResponse item = OrderItemResponse.builder()
                .productName("Test Product")
                .sellerId("seller-1")
                .price(new BigDecimal("50.00"))
                .quantity(2)
                .build();

        OrderResponse response = OrderResponse.builder()
                .orderNumber("ORD-12345")
                .status(OrderStatus.PENDING)
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("10.00"))
                .total(new BigDecimal("110.00"))
                .createdAt(now)
                .shippingAddress(address)
                .items(Arrays.asList(item))
                .build();

        assertEquals("ORD-12345", response.getOrderNumber());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(new BigDecimal("100.00"), response.getSubtotal());
        assertEquals(new BigDecimal("10.00"), response.getTax());
        assertEquals(new BigDecimal("110.00"), response.getTotal());
        assertEquals(now, response.getCreatedAt());
        assertNotNull(response.getShippingAddress());
        assertEquals(1, response.getItems().size());
    }

    @Test
    void testOrderResponseSetters() {
        OrderResponse response = OrderResponse.builder()
                .orderNumber("ORD-12345")
                .status(OrderStatus.PENDING)
                .subtotal(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();
        response.setOrderNumber("ORD-67890");
        response.setStatus(OrderStatus.SHIPPED);
        response.setSubtotal(new BigDecimal("200.00"));
        response.setTax(new BigDecimal("20.00"));
        response.setTotal(new BigDecimal("220.00"));
        response.setCreatedAt(LocalDateTime.now());
        response.setShippingAddress(null);
        response.setItems(Arrays.asList());

        assertEquals("ORD-67890", response.getOrderNumber());
        assertEquals(OrderStatus.SHIPPED, response.getStatus());
        assertEquals(new BigDecimal("200.00"), response.getSubtotal());
    }

    @Test
    void testOrderResponseEqualsAndHashCode() {
        OrderResponse response1 = OrderResponse.builder()
                .orderNumber("ORD-123")
                .status(OrderStatus.PENDING)
                .total(new BigDecimal("100.00"))
                .build();

        OrderResponse response2 = OrderResponse.builder()
                .orderNumber("ORD-123")
                .status(OrderStatus.PENDING)
                .total(new BigDecimal("100.00"))
                .build();

        OrderResponse response3 = OrderResponse.builder()
                .orderNumber("ORD-456")
                .status(OrderStatus.CONFIRMED)
                .build();

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testOrderResponseToString() {
        OrderResponse response = OrderResponse.builder()
                .orderNumber("ORD-12345")
                .status(OrderStatus.PENDING)
                .total(new BigDecimal("100.00"))
                .build();

        String toString = response.toString();
        assertTrue(toString.contains("ORD-12345"));
        assertTrue(toString.contains("PENDING"));
    }

    @Test
    void testOrderResponseWithEmptyItems() {
        OrderResponse response = OrderResponse.builder()
                .orderNumber("ORD-123")
                .items(Arrays.asList())
                .build();

        assertTrue(response.getItems().isEmpty());
    }

    @Test
    void testOrderResponseAllStatuses() {
        for (OrderStatus status : OrderStatus.values()) {
            OrderResponse response = OrderResponse.builder()
                    .orderNumber("ORD-123")
                    .status(status)
                    .build();
            
            assertEquals(status, response.getStatus());
        }
    }
}
