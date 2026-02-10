package com.buyone.orderservice.dto.response.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartResponseTests {

    @Test
    void testCartResponseBuilder() {
        CartItemResponse item = CartItemResponse.builder()
                .productId("prod-1")
                .productName("Test Product")
                .price(new BigDecimal("10.00"))
                .quantity(2)
                .lineTotal(new BigDecimal("20.00"))
                .build();

        List<CartItemResponse> items = Arrays.asList(item);

        CartResponse response = CartResponse.builder()
                .id("cart-1")
                .userId("user-1")
                .items(items)
                .subtotal(new BigDecimal("20.00"))
                .tax(new BigDecimal("2.00"))
                .total(new BigDecimal("22.00"))
                .build();

        assertEquals("cart-1", response.getId());
        assertEquals("user-1", response.getUserId());
        assertEquals(1, response.getItems().size());
        assertEquals(new BigDecimal("20.00"), response.getSubtotal());
        assertEquals(new BigDecimal("2.00"), response.getTax());
        assertEquals(new BigDecimal("22.00"), response.getTotal());
    }

    @Test
    void testCartResponseSetters() {
        CartResponse response = CartResponse.builder()
                .id("cart-1")
                .userId("user-1")
                .items(Arrays.asList())
                .subtotal(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();
        response.setId("cart-2");
        response.setUserId("user-2");
        response.setItems(Arrays.asList());
        response.setSubtotal(new BigDecimal("50.00"));
        response.setTax(new BigDecimal("5.00"));
        response.setTotal(new BigDecimal("55.00"));

        assertEquals("cart-2", response.getId());
        assertEquals("user-2", response.getUserId());
        assertEquals(new BigDecimal("50.00"), response.getSubtotal());
    }

    @Test
    void testCartResponseEqualsAndHashCode() {
        CartResponse response1 = CartResponse.builder()
                .id("cart-1")
                .userId("user-1")
                .subtotal(new BigDecimal("100.00"))
                .build();

        CartResponse response2 = CartResponse.builder()
                .id("cart-1")
                .userId("user-1")
                .subtotal(new BigDecimal("100.00"))
                .build();

        CartResponse response3 = CartResponse.builder()
                .id("cart-2")
                .userId("user-2")
                .build();

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testCartResponseToString() {
        CartResponse response = CartResponse.builder()
                .id("cart-1")
                .userId("user-1")
                .total(new BigDecimal("100.00"))
                .build();

        String toString = response.toString();
        assertTrue(toString.contains("cart-1"));
        assertTrue(toString.contains("user-1"));
    }

    @Test
    void testCartResponseWithEmptyItems() {
        CartResponse response = CartResponse.builder()
                .id("cart-1")
                .userId("user-1")
                .items(Arrays.asList())
                .subtotal(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();

        assertTrue(response.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getSubtotal());
    }
}
