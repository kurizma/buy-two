package com.buyone.productservice.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProductTests {

    @Test
    void testNoArgsConstructor() {
        Product product = new Product();
        assertNotNull(product);
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getPrice());
        assertNull(product.getQuantity());
        assertNull(product.getUserId());
        assertNull(product.getCategoryId());
        assertNull(product.getImages());
    }

    @Test
    void testAllArgsConstructor() {
        List<String> images = List.of("img1.jpg", "img2.jpg");
        Product product = new Product("1", "Test Product", "Description", 
                new BigDecimal("99.99"), 10, "user123", "cat1", images);
        
        assertEquals("1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Description", product.getDescription());
        assertEquals(new BigDecimal("99.99"), product.getPrice());
        assertEquals(10, product.getQuantity());
        assertEquals("user123", product.getUserId());
        assertEquals("cat1", product.getCategoryId());
        assertEquals(2, product.getImages().size());
    }

    @Test
    void testBuilder() {
        Product product = Product.builder()
                .id("2")
                .name("Builder Product")
                .description("Built via builder")
                .price(new BigDecimal("49.99"))
                .quantity(5)
                .userId("seller456")
                .categoryId("cat2")
                .images(List.of("image.png"))
                .build();
        
        assertEquals("2", product.getId());
        assertEquals("Builder Product", product.getName());
        assertEquals("Built via builder", product.getDescription());
        assertEquals(new BigDecimal("49.99"), product.getPrice());
        assertEquals(5, product.getQuantity());
        assertEquals("seller456", product.getUserId());
        assertEquals("cat2", product.getCategoryId());
        assertEquals(1, product.getImages().size());
    }

    @Test
    void testSettersAndGetters() {
        Product product = new Product();
        
        product.setId("3");
        product.setName("Setter Product");
        product.setDescription("Set via setters");
        product.setPrice(new BigDecimal("19.99"));
        product.setQuantity(100);
        product.setUserId("user789");
        product.setCategoryId("cat3");
        product.setImages(List.of("a.jpg", "b.jpg", "c.jpg"));
        
        assertEquals("3", product.getId());
        assertEquals("Setter Product", product.getName());
        assertEquals("Set via setters", product.getDescription());
        assertEquals(new BigDecimal("19.99"), product.getPrice());
        assertEquals(100, product.getQuantity());
        assertEquals("user789", product.getUserId());
        assertEquals("cat3", product.getCategoryId());
        assertEquals(3, product.getImages().size());
    }

    @Test
    void testEqualsAndHashCode() {
        Product product1 = new Product("1", "P1", "D1", 
                new BigDecimal("10.00"), 5, "u1", "c1", List.of());
        Product product2 = new Product("1", "P1", "D1", 
                new BigDecimal("10.00"), 5, "u1", "c1", List.of());
        Product product3 = new Product("2", "P2", "D2", 
                new BigDecimal("20.00"), 10, "u2", "c2", List.of());
        
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
        assertNotEquals(product1, product3);
    }

    @Test
    void testToString() {
        Product product = Product.builder()
                .id("1")
                .name("Test")
                .price(new BigDecimal("9.99"))
                .build();
        String str = product.toString();
        assertNotNull(str);
        assertTrue(str.contains("1"));
        assertTrue(str.contains("Test"));
    }

    @Test
    void testNullImages() {
        Product product = Product.builder()
                .id("1")
                .name("No Images")
                .build();
        assertNull(product.getImages());
    }

    @Test
    void testEmptyImages() {
        Product product = Product.builder()
                .images(List.of())
                .build();
        assertTrue(product.getImages().isEmpty());
    }
}
