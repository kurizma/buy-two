package com.buyone.orderservice.model.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTests {

    @Test
    void testCartItemBuilder() {
        CartItem item = CartItem.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .productName("Test Product")
                .price(new BigDecimal("25.00"))
                .quantity(2)
                .imageUrl("http://example.com/image.jpg")
                .build();

        assertEquals("prod-1", item.getProductId());
        assertEquals("seller-1", item.getSellerId());
        assertEquals("Test Product", item.getProductName());
        assertEquals(new BigDecimal("25.00"), item.getPrice());
        assertEquals(2, item.getQuantity());
        assertEquals("http://example.com/image.jpg", item.getImageUrl());
    }

    @Test
    void testCartItemNoArgsConstructor() {
        CartItem item = new CartItem();
        assertNull(item.getProductId());
        assertNull(item.getSellerId());
        assertEquals(1, item.getQuantity()); // Default value
    }

    @Test
    void testCartItemAllArgsConstructor() {
        CartItem item = new CartItem("prod-1", "seller-1", "Test Product",
                new BigDecimal("25.00"), 3, "http://example.com/image.jpg");

        assertEquals("prod-1", item.getProductId());
        assertEquals("seller-1", item.getSellerId());
        assertEquals("Test Product", item.getProductName());
        assertEquals(new BigDecimal("25.00"), item.getPrice());
        assertEquals(3, item.getQuantity());
    }

    @Test
    void testCartItemSetters() {
        CartItem item = new CartItem();
        item.setProductId("prod-2");
        item.setSellerId("seller-2");
        item.setProductName("Another Product");
        item.setPrice(new BigDecimal("50.00"));
        item.setQuantity(5);
        item.setImageUrl("http://example.com/image2.jpg");

        assertEquals("prod-2", item.getProductId());
        assertEquals("seller-2", item.getSellerId());
        assertEquals("Another Product", item.getProductName());
        assertEquals(new BigDecimal("50.00"), item.getPrice());
        assertEquals(5, item.getQuantity());
    }

    @Test
    void testCartItemEqualsAndHashCode() {
        CartItem item1 = CartItem.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .productName("Test")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();
        
        CartItem item2 = CartItem.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .productName("Test")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();

        CartItem item3 = CartItem.builder()
                .productId("prod-2")
                .sellerId("seller-2")
                .build();

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1, item3);
    }

    @Test
    void testCartItemToString() {
        CartItem item = CartItem.builder()
                .productId("prod-1")
                .productName("Test Product")
                .build();
        
        String toString = item.toString();
        assertTrue(toString.contains("prod-1"));
        assertTrue(toString.contains("Test Product"));
    }

    @Test
    void testCartItemWithNullImageUrl() {
        CartItem item = CartItem.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .productName("Test Product")
                .price(new BigDecimal("25.00"))
                .quantity(1)
                .build();

        assertNull(item.getImageUrl());
    }

    @Test
    void testCartItemPriceCalculation() {
        CartItem item = CartItem.builder()
                .productId("prod-1")
                .price(new BigDecimal("10.50"))
                .quantity(3)
                .build();

        BigDecimal total = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        assertEquals(new BigDecimal("31.50"), total);
    }
}
