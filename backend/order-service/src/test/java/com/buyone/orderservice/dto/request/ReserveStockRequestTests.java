package com.buyone.orderservice.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReserveStockRequestTests {

    @Test
    void testReserveStockRequestAllArgsConstructor() {
        ReserveStockRequest request = new ReserveStockRequest("prod-1", 5, "ORD-12345");

        assertEquals("prod-1", request.getProductId());
        assertEquals(5, request.getQuantity());
        assertEquals("ORD-12345", request.getOrderNumber());
    }

    @Test
    void testReserveStockRequestNoArgsConstructor() {
        ReserveStockRequest request = new ReserveStockRequest();

        assertNull(request.getProductId());
        assertEquals(0, request.getQuantity());
        assertNull(request.getOrderNumber());
    }

    @Test
    void testReserveStockRequestSetters() {
        ReserveStockRequest request = new ReserveStockRequest();
        request.setProductId("prod-2");
        request.setQuantity(10);
        request.setOrderNumber("ORD-67890");

        assertEquals("prod-2", request.getProductId());
        assertEquals(10, request.getQuantity());
        assertEquals("ORD-67890", request.getOrderNumber());
    }

    @Test
    void testReserveStockRequestEqualsAndHashCode() {
        ReserveStockRequest request1 = new ReserveStockRequest("prod-1", 5, "ORD-123");
        ReserveStockRequest request2 = new ReserveStockRequest("prod-1", 5, "ORD-123");
        ReserveStockRequest request3 = new ReserveStockRequest("prod-2", 10, "ORD-456");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void testReserveStockRequestToString() {
        ReserveStockRequest request = new ReserveStockRequest("prod-1", 5, "ORD-12345");

        String toString = request.toString();
        assertTrue(toString.contains("prod-1"));
        assertTrue(toString.contains("5"));
        assertTrue(toString.contains("ORD-12345"));
    }
}
