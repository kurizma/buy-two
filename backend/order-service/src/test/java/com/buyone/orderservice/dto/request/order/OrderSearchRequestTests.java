package com.buyone.orderservice.dto.request.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderSearchRequestTests {

    @Test
    void testOrderSearchRequestBuilder() {
        OrderSearchRequest request = OrderSearchRequest.builder()
                .keyword("test")
                .status("PENDING")
                .startDate("2025-01-01T00:00:00")
                .endDate("2025-12-31T23:59:59")
                .page(0)
                .size(20)
                .build();

        assertEquals("test", request.getKeyword());
        assertEquals("PENDING", request.getStatus());
        assertEquals("2025-01-01T00:00:00", request.getStartDate());
        assertEquals("2025-12-31T23:59:59", request.getEndDate());
        assertEquals(0, request.getPage());
        assertEquals(20, request.getSize());
    }

    @Test
    void testOrderSearchRequestDefaults() {
        OrderSearchRequest request = OrderSearchRequest.builder().build();

        assertEquals(0, request.getPage());
        assertEquals(10, request.getSize());
    }

    @Test
    void testOrderSearchRequestSetters() {
        OrderSearchRequest request = OrderSearchRequest.builder().build();
        request.setKeyword("product");
        request.setStatus("SHIPPED");
        request.setStartDate("2025-06-01T00:00:00");
        request.setEndDate("2025-06-30T23:59:59");
        request.setPage(2);
        request.setSize(50);

        assertEquals("product", request.getKeyword());
        assertEquals("SHIPPED", request.getStatus());
        assertEquals(2, request.getPage());
        assertEquals(50, request.getSize());
    }

    @Test
    void testOrderSearchRequestEqualsAndHashCode() {
        OrderSearchRequest request1 = OrderSearchRequest.builder()
                .keyword("test")
                .status("PENDING")
                .build();

        OrderSearchRequest request2 = OrderSearchRequest.builder()
                .keyword("test")
                .status("PENDING")
                .build();

        OrderSearchRequest request3 = OrderSearchRequest.builder()
                .keyword("different")
                .status("SHIPPED")
                .build();

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void testOrderSearchRequestToString() {
        OrderSearchRequest request = OrderSearchRequest.builder()
                .keyword("test")
                .status("PENDING")
                .build();

        String toString = request.toString();
        assertTrue(toString.contains("test"));
        assertTrue(toString.contains("PENDING"));
    }

    @Test
    void testOrderSearchRequestWithNullKeyword() {
        OrderSearchRequest request = OrderSearchRequest.builder()
                .status("CONFIRMED")
                .build();

        assertNull(request.getKeyword());
        assertEquals("CONFIRMED", request.getStatus());
    }

    @Test
    void testOrderSearchRequestWithCustomPagination() {
        OrderSearchRequest request = OrderSearchRequest.builder()
                .page(5)
                .size(100)
                .build();

        assertEquals(5, request.getPage());
        assertEquals(100, request.getSize());
    }
}
