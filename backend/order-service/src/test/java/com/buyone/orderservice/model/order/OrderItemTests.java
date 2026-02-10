package com.buyone.orderservice.model.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTests {

    @Test
    void testOrderItemBuilder() {
        OrderItem item = OrderItem.builder()
                .productId("prod-1")
                .productName("Test Product")
                .sellerId("seller-1")
                .price(new BigDecimal("25.00"))
                .quantity(2)
                .imageUrl("http://example.com/image.jpg")
                .build();

        assertEquals("prod-1", item.getProductId());
        assertEquals("Test Product", item.getProductName());
        assertEquals("seller-1", item.getSellerId());
        assertEquals(new BigDecimal("25.00"), item.getPrice());
        assertEquals(2, item.getQuantity());
        assertEquals("http://example.com/image.jpg", item.getImageUrl());
    }

    @Test
    void testOrderItemNoArgsConstructor() {
        OrderItem item = new OrderItem();
        assertNull(item.getProductId());
        assertNull(item.getProductName());
        assertNull(item.getSellerId());
        assertNull(item.getPrice());
        assertEquals(0, item.getQuantity());
    }

    @Test
    void testOrderItemAllArgsConstructor() {
        OrderItem item = new OrderItem("prod-1", "Test Product", "seller-1",
                new BigDecimal("25.00"), 3, "http://example.com/image.jpg");

        assertEquals("prod-1", item.getProductId());
        assertEquals("Test Product", item.getProductName());
        assertEquals("seller-1", item.getSellerId());
        assertEquals(new BigDecimal("25.00"), item.getPrice());
        assertEquals(3, item.getQuantity());
    }

    @Test
    void testOrderItemSetters() {
        OrderItem item = new OrderItem();
        item.setProductId("prod-2");
        item.setProductName("Another Product");
        item.setSellerId("seller-2");
        item.setPrice(new BigDecimal("50.00"));
        item.setQuantity(5);
        item.setImageUrl("http://example.com/image2.jpg");

        assertEquals("prod-2", item.getProductId());
        assertEquals("Another Product", item.getProductName());
        assertEquals("seller-2", item.getSellerId());
        assertEquals(new BigDecimal("50.00"), item.getPrice());
        assertEquals(5, item.getQuantity());
    }

    @Test
    void testOrderItemEqualsAndHashCode() {
        OrderItem item1 = OrderItem.builder()
                .productId("prod-1")
                .productName("Test")
                .sellerId("seller-1")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();

        OrderItem item2 = OrderItem.builder()
                .productId("prod-1")
                .productName("Test")
                .sellerId("seller-1")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .build();

        OrderItem item3 = OrderItem.builder()
                .productId("prod-2")
                .sellerId("seller-2")
                .build();

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1, item3);
    }

    @Test
    void testOrderItemToString() {
        OrderItem item = OrderItem.builder()
                .productId("prod-1")
                .productName("Test Product")
                .build();

        String toString = item.toString();
        assertTrue(toString.contains("prod-1"));
        assertTrue(toString.contains("Test Product"));
    }

    @Test
    void testOrderItemLineTotal() {
        OrderItem item = OrderItem.builder()
                .productId("prod-1")
                .price(new BigDecimal("10.50"))
                .quantity(3)
                .build();

        BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        assertEquals(new BigDecimal("31.50"), lineTotal);
    }

    @Test
    void testOrderItemWithNullImageUrl() {
        OrderItem item = OrderItem.builder()
                .productId("prod-1")
                .productName("Test Product")
                .quantity(1)
                .build();

        assertNull(item.getImageUrl());
    }
}
