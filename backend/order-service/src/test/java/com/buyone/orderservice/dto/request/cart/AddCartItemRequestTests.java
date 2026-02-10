package com.buyone.orderservice.dto.request.cart;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddCartItemRequestTests {

    @Test
    void testAddCartItemRequestBuilder() {
        AddCartItemRequest request = AddCartItemRequest.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .quantity(2)
                .build();

        assertEquals("prod-1", request.getProductId());
        assertEquals("seller-1", request.getSellerId());
        assertEquals(2, request.getQuantity());
    }

    @Test
    void testAddCartItemRequestSetters() {
        AddCartItemRequest request = AddCartItemRequest.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .quantity(1)
                .build();
        request.setProductId("prod-2");
        request.setSellerId("seller-2");
        request.setQuantity(5);

        assertEquals("prod-2", request.getProductId());
        assertEquals("seller-2", request.getSellerId());
        assertEquals(5, request.getQuantity());
    }

    @Test
    void testAddCartItemRequestEqualsAndHashCode() {
        AddCartItemRequest request1 = AddCartItemRequest.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .quantity(1)
                .build();

        AddCartItemRequest request2 = AddCartItemRequest.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .quantity(1)
                .build();

        AddCartItemRequest request3 = AddCartItemRequest.builder()
                .productId("prod-2")
                .sellerId("seller-2")
                .quantity(2)
                .build();

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void testAddCartItemRequestToString() {
        AddCartItemRequest request = AddCartItemRequest.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .quantity(3)
                .build();

        String toString = request.toString();
        assertTrue(toString.contains("prod-1"));
        assertTrue(toString.contains("seller-1"));
        assertTrue(toString.contains("3"));
    }

    @Test
    void testAddCartItemRequestWithMinQuantity() {
        AddCartItemRequest request = AddCartItemRequest.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .quantity(1)
                .build();

        assertEquals(1, request.getQuantity());
    }

    @Test
    void testAddCartItemRequestWithLargeQuantity() {
        AddCartItemRequest request = AddCartItemRequest.builder()
                .productId("prod-1")
                .sellerId("seller-1")
                .quantity(1000)
                .build();

        assertEquals(1000, request.getQuantity());
    }
}
