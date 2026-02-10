package com.buyone.productservice.event;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductCreatedEventTests {

    @Test
    void testNoArgsConstructor() {
        ProductCreatedEvent event = new ProductCreatedEvent();
        assertNotNull(event);
        assertNull(event.getProductId());
        assertNull(event.getSellerId());
        assertNull(event.getName());
        assertNull(event.getPrice());
    }

    @Test
    void testAllArgsConstructor() {
        ProductCreatedEvent event = new ProductCreatedEvent("prod1", "seller1", "Product", new BigDecimal("99.99"));
        
        assertEquals("prod1", event.getProductId());
        assertEquals("seller1", event.getSellerId());
        assertEquals("Product", event.getName());
        assertEquals(new BigDecimal("99.99"), event.getPrice());
    }

    @Test
    void testBuilder() {
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId("p1")
                .sellerId("s1")
                .name("Built Product")
                .price(new BigDecimal("49.99"))
                .build();
        
        assertEquals("p1", event.getProductId());
        assertEquals("s1", event.getSellerId());
        assertEquals("Built Product", event.getName());
        assertEquals(new BigDecimal("49.99"), event.getPrice());
    }

    @Test
    void testSettersAndGetters() {
        ProductCreatedEvent event = new ProductCreatedEvent();
        
        event.setProductId("prod2");
        event.setSellerId("seller2");
        event.setName("Setter Product");
        event.setPrice(new BigDecimal("19.99"));
        
        assertEquals("prod2", event.getProductId());
        assertEquals("seller2", event.getSellerId());
        assertEquals("Setter Product", event.getName());
        assertEquals(new BigDecimal("19.99"), event.getPrice());
    }

    @Test
    void testEqualsAndHashCode() {
        ProductCreatedEvent event1 = new ProductCreatedEvent("p1", "s1", "Name", new BigDecimal("10.00"));
        ProductCreatedEvent event2 = new ProductCreatedEvent("p1", "s1", "Name", new BigDecimal("10.00"));
        ProductCreatedEvent event3 = new ProductCreatedEvent("p2", "s2", "Other", new BigDecimal("20.00"));
        
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
    }

    @Test
    void testToString() {
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId("testId")
                .name("Test")
                .build();
        String str = event.toString();
        assertNotNull(str);
        assertTrue(str.contains("testId"));
    }
}
