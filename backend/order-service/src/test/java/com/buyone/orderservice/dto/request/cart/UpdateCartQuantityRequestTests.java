package com.buyone.orderservice.dto.request.cart;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateCartQuantityRequestTests {

    @Test
    void testUpdateCartQuantityRequestCreation() {
        UpdateCartQuantityRequest request = new UpdateCartQuantityRequest();
        request.setQuantity(5);

        assertEquals(5, request.getQuantity());
    }

    @Test
    void testUpdateCartQuantityRequestSetters() {
        UpdateCartQuantityRequest request = new UpdateCartQuantityRequest();
        request.setQuantity(10);

        assertEquals(10, request.getQuantity());
    }

    @Test
    void testUpdateCartQuantityRequestEqualsAndHashCode() {
        UpdateCartQuantityRequest request1 = new UpdateCartQuantityRequest();
        request1.setQuantity(5);

        UpdateCartQuantityRequest request2 = new UpdateCartQuantityRequest();
        request2.setQuantity(5);

        UpdateCartQuantityRequest request3 = new UpdateCartQuantityRequest();
        request3.setQuantity(10);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void testUpdateCartQuantityRequestToString() {
        UpdateCartQuantityRequest request = new UpdateCartQuantityRequest();
        request.setQuantity(3);

        String toString = request.toString();
        assertTrue(toString.contains("3"));
    }

    @Test
    void testUpdateCartQuantityRequestWithMinQuantity() {
        UpdateCartQuantityRequest request = new UpdateCartQuantityRequest();
        request.setQuantity(1);

        assertEquals(1, request.getQuantity());
    }

    @Test
    void testUpdateCartQuantityRequestWithLargeQuantity() {
        UpdateCartQuantityRequest request = new UpdateCartQuantityRequest();
        request.setQuantity(9999);

        assertEquals(9999, request.getQuantity());
    }
}
