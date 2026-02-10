package com.buyone.productservice.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductDeletedEventTests {

    @Test
    void testNoArgsConstructor() {
        ProductDeletedEvent event = new ProductDeletedEvent();
        assertNotNull(event);
        assertNull(event.getProductId());
        assertNull(event.getSellerId());
    }

    @Test
    void testAllArgsConstructor() {
        ProductDeletedEvent event = new ProductDeletedEvent("prod1", "seller1");
        
        assertEquals("prod1", event.getProductId());
        assertEquals("seller1", event.getSellerId());
    }

    @Test
    void testBuilder() {
        ProductDeletedEvent event = ProductDeletedEvent.builder()
                .productId("p1")
                .sellerId("s1")
                .build();
        
        assertEquals("p1", event.getProductId());
        assertEquals("s1", event.getSellerId());
    }

    @Test
    void testSettersAndGetters() {
        ProductDeletedEvent event = new ProductDeletedEvent();
        
        event.setProductId("prod2");
        event.setSellerId("seller2");
        
        assertEquals("prod2", event.getProductId());
        assertEquals("seller2", event.getSellerId());
    }

    @Test
    void testEqualsAndHashCode() {
        ProductDeletedEvent event1 = new ProductDeletedEvent("p1", "s1");
        ProductDeletedEvent event2 = new ProductDeletedEvent("p1", "s1");
        ProductDeletedEvent event3 = new ProductDeletedEvent("p2", "s2");
        
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
    }

    @Test
    void testToString() {
        ProductDeletedEvent event = ProductDeletedEvent.builder()
                .productId("testId")
                .sellerId("testSeller")
                .build();
        String str = event.toString();
        assertNotNull(str);
        assertTrue(str.contains("testId"));
    }
}
