package com.buyone.orderservice.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTests {

    @Test
    void testProductBuilder() {
        List<String> images = Arrays.asList("image1.jpg", "image2.jpg");
        
        Product product = Product.builder()
                .id("prod-1")
                .name("Test Product")
                .description("A test product")
                .price(new BigDecimal("99.99"))
                .quantity(100)
                .userId("seller-1")
                .categoryId("cat-1")
                .images(images)
                .build();

        assertEquals("prod-1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("A test product", product.getDescription());
        assertEquals(new BigDecimal("99.99"), product.getPrice());
        assertEquals(100, product.getQuantity());
        assertEquals("seller-1", product.getUserId());
        assertEquals("cat-1", product.getCategoryId());
        assertEquals(2, product.getImages().size());
    }

    @Test
    void testProductNoArgsConstructor() {
        Product product = new Product();
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
    void testProductAllArgsConstructor() {
        List<String> images = Arrays.asList("image1.jpg");
        
        Product product = new Product("prod-1", "Test Product", "Description",
                new BigDecimal("50.00"), 50, "seller-1", "cat-1", images);

        assertEquals("prod-1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Description", product.getDescription());
        assertEquals(new BigDecimal("50.00"), product.getPrice());
        assertEquals(50, product.getQuantity());
        assertEquals("seller-1", product.getUserId());
        assertEquals("cat-1", product.getCategoryId());
    }

    @Test
    void testProductSetters() {
        Product product = new Product();
        product.setId("prod-2");
        product.setName("Another Product");
        product.setDescription("Another description");
        product.setPrice(new BigDecimal("150.00"));
        product.setQuantity(200);
        product.setUserId("seller-2");
        product.setCategoryId("cat-2");
        product.setImages(Arrays.asList("img.jpg"));

        assertEquals("prod-2", product.getId());
        assertEquals("Another Product", product.getName());
        assertEquals(new BigDecimal("150.00"), product.getPrice());
        assertEquals(200, product.getQuantity());
    }

    @Test
    void testProductEqualsAndHashCode() {
        Product product1 = Product.builder()
                .id("prod-1")
                .name("Test")
                .price(new BigDecimal("10.00"))
                .build();

        Product product2 = Product.builder()
                .id("prod-1")
                .name("Test")
                .price(new BigDecimal("10.00"))
                .build();

        Product product3 = Product.builder()
                .id("prod-2")
                .name("Different")
                .build();

        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
        assertNotEquals(product1, product3);
    }

    @Test
    void testProductToString() {
        Product product = Product.builder()
                .id("prod-1")
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        String toString = product.toString();
        assertTrue(toString.contains("prod-1"));
        assertTrue(toString.contains("Test Product"));
        assertTrue(toString.contains("99.99"));
    }

    @Test
    void testProductWithEmptyImages() {
        Product product = Product.builder()
                .id("prod-1")
                .name("Test")
                .images(Arrays.asList())
                .build();

        assertNotNull(product.getImages());
        assertTrue(product.getImages().isEmpty());
    }
}
