package com.buyone.orderservice.dto.response.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemResponseTests {

    @Test
    void testOrderItemResponseBuilder() {
        OrderItemResponse response = OrderItemResponse.builder()
                .productName("Test Product")
                .sellerId("seller-1")
                .price(new BigDecimal("25.00"))
                .quantity(2)
                .build();

        assertEquals("Test Product", response.getProductName());
        assertEquals("seller-1", response.getSellerId());
        assertEquals(new BigDecimal("25.00"), response.getPrice());
        assertEquals(2, response.getQuantity());
    }

    @Test
    void testOrderItemResponseSetters() {
        OrderItemResponse response = OrderItemResponse.builder()
                .productName("Test")
                .sellerId("seller-1")
                .price(BigDecimal.ZERO)
                .quantity(1)
                .build();
        response.setProductName("Another Product");
        response.setSellerId("seller-2");
        response.setPrice(new BigDecimal("30.00"));
        response.setQuantity(3);

        assertEquals("Another Product", response.getProductName());
        assertEquals("seller-2", response.getSellerId());
        assertEquals(new BigDecimal("30.00"), response.getPrice());
        assertEquals(3, response.getQuantity());
    }

    @Test
    void testOrderItemResponseEqualsAndHashCode() {
        OrderItemResponse response1 = OrderItemResponse.builder()
                .productName("Test")
                .sellerId("seller-1")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();

        OrderItemResponse response2 = OrderItemResponse.builder()
                .productName("Test")
                .sellerId("seller-1")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();

        OrderItemResponse response3 = OrderItemResponse.builder()
                .productName("Different")
                .sellerId("seller-2")
                .build();

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testOrderItemResponseToString() {
        OrderItemResponse response = OrderItemResponse.builder()
                .productName("Test Product")
                .sellerId("seller-1")
                .price(new BigDecimal("25.00"))
                .build();

        String toString = response.toString();
        assertTrue(toString.contains("Test Product"));
        assertTrue(toString.contains("seller-1"));
    }

    @Test
    void testOrderItemResponseLineTotal() {
        OrderItemResponse response = OrderItemResponse.builder()
                .price(new BigDecimal("15.00"))
                .quantity(4)
                .build();

        BigDecimal lineTotal = response.getPrice().multiply(BigDecimal.valueOf(response.getQuantity()));
        assertEquals(new BigDecimal("60.00"), lineTotal);
    }
}
