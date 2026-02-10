package com.buyone.productservice.event;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductUpdatedEventTests {

    @Test
    void testNoArgsConstructor() {
        ProductUpdatedEvent event = new ProductUpdatedEvent();
        assertNotNull(event);
        assertNull(event.getProductId());
        assertNull(event.getSellerId());
        assertNull(event.getName());
        assertNull(event.getPrice());
    }

    @Test
    void testAllArgsConstructor() {
        ProductUpdatedEvent event = new ProductUpdatedEvent("prod1", "seller1", "Updated Product", new BigDecimal("149.99"));
        
        assertEquals("prod1", event.getProductId());
        assertEquals("seller1", event.getSellerId());
        assertEquals("Updated Product", event.getName());
        assertEquals(new BigDecimal("149.99"), event.getPrice());
    }

    @Test
    void testBuilder() {
        ProductUpdatedEvent event = ProductUpdatedEvent.builder()
                .productId("p1")
                .sellerId("s1")
                .name("Built Updated")
                .price(new BigDecimal("79.99"))
                .build();
        
        assertEquals("p1", event.getProductId());
        assertEquals("s1", event.getSellerId());
        assertEquals("Built Updated", event.getName());
        assertEquals(new BigDecimal("79.99"), event.getPrice());
    }

    @Test
    void testSettersAndGetters() {
        ProductUpdatedEvent event = new ProductUpdatedEvent();
        
        event.setProductId("prod2");
        event.setSellerId("seller2");
        event.setName("Setter Updated");
        event.setPrice(new BigDecimal("29.99"));
        
        assertEquals("prod2", event.getProductId());
        assertEquals("seller2", event.getSellerId());
        assertEquals("Setter Updated", event.getName());
        assertEquals(new BigDecimal("29.99"), event.getPrice());
    }

    @Test
    void testEqualsAndHashCode() {
        ProductUpdatedEvent event1 = new ProductUpdatedEvent("p1", "s1", "Name", new BigDecimal("10.00"));
        ProductUpdatedEvent event2 = new ProductUpdatedEvent("p1", "s1", "Name", new BigDecimal("10.00"));
        ProductUpdatedEvent event3 = new ProductUpdatedEvent("p2", "s2", "Other", new BigDecimal("20.00"));
        
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
    }

    @Test
    void testToString() {
        ProductUpdatedEvent event = ProductUpdatedEvent.builder()
                .productId("testId")
                .name("Test")
                .build();
        String str = event.toString();
        assertNotNull(str);
        assertTrue(str.contains("testId"));
    }
}
