package com.buyone.orderservice.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReleaseStockRequestTests {

    @Test
    void testReleaseStockRequestAllArgsConstructor() {
        ReleaseStockRequest request = new ReleaseStockRequest("prod-1", 5);

        assertEquals("prod-1", request.getProductId());
        assertEquals(5, request.getQuantity());
    }

    @Test
    void testReleaseStockRequestNoArgsConstructor() {
        ReleaseStockRequest request = new ReleaseStockRequest();

        assertNull(request.getProductId());
        assertEquals(0, request.getQuantity());
    }

    @Test
    void testReleaseStockRequestSetters() {
        ReleaseStockRequest request = new ReleaseStockRequest();
        request.setProductId("prod-2");
        request.setQuantity(10);

        assertEquals("prod-2", request.getProductId());
        assertEquals(10, request.getQuantity());
    }

    @Test
    void testReleaseStockRequestEqualsAndHashCode() {
        ReleaseStockRequest request1 = new ReleaseStockRequest("prod-1", 5);
        ReleaseStockRequest request2 = new ReleaseStockRequest("prod-1", 5);
        ReleaseStockRequest request3 = new ReleaseStockRequest("prod-2", 10);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void testReleaseStockRequestToString() {
        ReleaseStockRequest request = new ReleaseStockRequest("prod-1", 5);

        String toString = request.toString();
        assertTrue(toString.contains("prod-1"));
        assertTrue(toString.contains("5"));
    }
}
