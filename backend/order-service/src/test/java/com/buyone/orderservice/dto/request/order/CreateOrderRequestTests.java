package com.buyone.orderservice.dto.request.order;

import com.buyone.orderservice.model.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateOrderRequestTests {

    @Test
    void testCreateOrderRequestBuilder() {
        Address address = Address.builder()
                .street("123 Main St")
                .city("New York")
                .zipCode("10001")
                .country("USA")
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .shippingAddress(address)
                .build();

        assertNotNull(request.getShippingAddress());
        assertEquals("123 Main St", request.getShippingAddress().getStreet());
    }

    @Test
    void testCreateOrderRequestNoArgsConstructor() {
        CreateOrderRequest request = new CreateOrderRequest();
        assertNull(request.getShippingAddress());
    }

    @Test
    void testCreateOrderRequestAllArgsConstructor() {
        Address address = Address.builder()
                .street("456 Oak Ave")
                .city("Los Angeles")
                .zipCode("90001")
                .country("USA")
                .build();

        CreateOrderRequest request = new CreateOrderRequest(address);

        assertNotNull(request.getShippingAddress());
        assertEquals("456 Oak Ave", request.getShippingAddress().getStreet());
    }

    @Test
    void testCreateOrderRequestSetters() {
        CreateOrderRequest request = new CreateOrderRequest();
        Address address = Address.builder()
                .street("789 Pine Rd")
                .city("Chicago")
                .zipCode("60601")
                .country("USA")
                .build();

        request.setShippingAddress(address);

        assertNotNull(request.getShippingAddress());
        assertEquals("789 Pine Rd", request.getShippingAddress().getStreet());
    }

    @Test
    void testCreateOrderRequestEqualsAndHashCode() {
        Address address = Address.builder()
                .street("123 Main St")
                .city("New York")
                .build();

        CreateOrderRequest request1 = CreateOrderRequest.builder()
                .shippingAddress(address)
                .build();

        CreateOrderRequest request2 = CreateOrderRequest.builder()
                .shippingAddress(address)
                .build();

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testCreateOrderRequestToString() {
        Address address = Address.builder()
                .street("123 Main St")
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .shippingAddress(address)
                .build();

        String toString = request.toString();
        assertTrue(toString.contains("123 Main St"));
    }
}
