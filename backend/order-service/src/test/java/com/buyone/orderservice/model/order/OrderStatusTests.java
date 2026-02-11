package com.buyone.orderservice.model.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTests {

    @Test
    void testOrderStatusValues() {
        OrderStatus[] statuses = OrderStatus.values();
        assertEquals(5, statuses.length);
    }

    @Test
    void testOrderStatusPending() {
        OrderStatus status = OrderStatus.PENDING;
        assertEquals("PENDING", status.name());
        assertEquals(0, status.ordinal());
    }

    @Test
    void testOrderStatusConfirmed() {
        OrderStatus status = OrderStatus.CONFIRMED;
        assertEquals("CONFIRMED", status.name());
        assertEquals(1, status.ordinal());
    }

    @Test
    void testOrderStatusShipped() {
        OrderStatus status = OrderStatus.SHIPPED;
        assertEquals("SHIPPED", status.name());
        assertEquals(2, status.ordinal());
    }

    @Test
    void testOrderStatusDelivered() {
        OrderStatus status = OrderStatus.DELIVERED;
        assertEquals("DELIVERED", status.name());
        assertEquals(3, status.ordinal());
    }

    @Test
    void testOrderStatusCancelled() {
        OrderStatus status = OrderStatus.CANCELLED;
        assertEquals("CANCELLED", status.name());
        assertEquals(4, status.ordinal());
    }

    @Test
    void testOrderStatusValueOf() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.valueOf("CONFIRMED"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.valueOf("DELIVERED"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
    }

    @Test
    void testOrderStatusInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("INVALID"));
    }
}
