package com.buyone.orderservice.dto.response.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CartItemResponseTests {

    @Test
    void testCartItemResponseBuilder() {
        CartItemResponse response = CartItemResponse.builder()
                .productId("prod-1")
                .productName("Test Product")
                .imageUrl("http://example.com/image.jpg")
                .sellerId("seller-1")
                .price(new BigDecimal("25.00"))
                .quantity(2)
                .lineTotal(new BigDecimal("50.00"))
                .build();

        assertEquals("prod-1", response.getProductId());
        assertEquals("Test Product", response.getProductName());
        assertEquals("http://example.com/image.jpg", response.getImageUrl());
        assertEquals("seller-1", response.getSellerId());
        assertEquals(new BigDecimal("25.00"), response.getPrice());
        assertEquals(2, response.getQuantity());
        assertEquals(new BigDecimal("50.00"), response.getLineTotal());
    }

    @Test
    void testCartItemResponseSetters() {
        CartItemResponse response = CartItemResponse.builder()
                .productId("prod-1")
                .productName("Test")
                .sellerId("seller-1")
                .price(BigDecimal.ZERO)
                .quantity(1)
                .lineTotal(BigDecimal.ZERO)
                .build();
        response.setProductId("prod-2");
        response.setProductName("Another Product");
        response.setImageUrl("http://example.com/image2.jpg");
        response.setSellerId("seller-2");
        response.setPrice(new BigDecimal("30.00"));
        response.setQuantity(3);
        response.setLineTotal(new BigDecimal("90.00"));

        assertEquals("prod-2", response.getProductId());
        assertEquals("Another Product", response.getProductName());
        assertEquals(new BigDecimal("30.00"), response.getPrice());
        assertEquals(3, response.getQuantity());
        assertEquals(new BigDecimal("90.00"), response.getLineTotal());
    }

    @Test
    void testCartItemResponseEqualsAndHashCode() {
        CartItemResponse response1 = CartItemResponse.builder()
                .productId("prod-1")
                .productName("Test")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();

        CartItemResponse response2 = CartItemResponse.builder()
                .productId("prod-1")
                .productName("Test")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();

        CartItemResponse response3 = CartItemResponse.builder()
                .productId("prod-2")
                .productName("Different")
                .build();

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testCartItemResponseToString() {
        CartItemResponse response = CartItemResponse.builder()
                .productId("prod-1")
                .productName("Test Product")
                .price(new BigDecimal("25.00"))
                .build();

        String toString = response.toString();
        assertTrue(toString.contains("prod-1"));
        assertTrue(toString.contains("Test Product"));
        assertTrue(toString.contains("25.00"));
    }

    @Test
    void testCartItemResponseWithNullImageUrl() {
        CartItemResponse response = CartItemResponse.builder()
                .productId("prod-1")
                .productName("Test Product")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();

        assertNull(response.getImageUrl());
    }

    @Test
    void testCartItemResponseLineTotalCalculation() {
        BigDecimal price = new BigDecimal("10.50");
        int quantity = 3;
        BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(quantity));

        CartItemResponse response = CartItemResponse.builder()
                .productId("prod-1")
                .price(price)
                .quantity(quantity)
                .lineTotal(lineTotal)
                .build();

        assertEquals(new BigDecimal("31.50"), response.getLineTotal());
    }
}
