package com.buyone.productservice.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ReservationTests {

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = Reservation.builder()
                .id("1")
                .productId("prod123")
                .quantity(5)
                .orderNumber("ORD-001")
                .createdAt(now)
                .build();
        
        assertEquals("1", reservation.getId());
        assertEquals("prod123", reservation.getProductId());
        assertEquals(5, reservation.getQuantity());
        assertEquals("ORD-001", reservation.getOrderNumber());
        assertEquals(now, reservation.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Reservation reservation = Reservation.builder().build();
        LocalDateTime now = LocalDateTime.now();
        
        reservation.setId("2");
        reservation.setProductId("prod456");
        reservation.setQuantity(10);
        reservation.setOrderNumber("ORD-002");
        reservation.setCreatedAt(now);
        
        assertEquals("2", reservation.getId());
        assertEquals("prod456", reservation.getProductId());
        assertEquals(10, reservation.getQuantity());
        assertEquals("ORD-002", reservation.getOrderNumber());
        assertEquals(now, reservation.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Reservation res1 = Reservation.builder()
                .id("1")
                .productId("p1")
                .quantity(5)
                .orderNumber("O1")
                .createdAt(now)
                .build();
        Reservation res2 = Reservation.builder()
                .id("1")
                .productId("p1")
                .quantity(5)
                .orderNumber("O1")
                .createdAt(now)
                .build();
        Reservation res3 = Reservation.builder()
                .id("2")
                .productId("p2")
                .quantity(10)
                .orderNumber("O2")
                .build();
        
        assertEquals(res1, res2);
        assertEquals(res1.hashCode(), res2.hashCode());
        assertNotEquals(res1, res3);
    }

    @Test
    void testToString() {
        Reservation reservation = Reservation.builder()
                .id("1")
                .productId("prod")
                .orderNumber("order")
                .build();
        String str = reservation.toString();
        assertNotNull(str);
        assertTrue(str.contains("prod") || str.contains("order"));
    }

    @Test
    void testNullCreatedAt() {
        Reservation reservation = Reservation.builder()
                .id("1")
                .productId("prod")
                .quantity(1)
                .orderNumber("ord")
                .build();
        assertNull(reservation.getCreatedAt());
    }

    @Test
    void testZeroQuantity() {
        Reservation reservation = Reservation.builder()
                .quantity(0)
                .build();
        assertEquals(0, reservation.getQuantity());
    }
}
