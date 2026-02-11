package com.buyone.orderservice.dto.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductResponseTests {

    @Test
    void testProductResponseCreation() {
        ProductResponse response = new ProductResponse();
        response.setId("prod-1");
        response.setName("Test Product");
        response.setDescription("A test product");
        response.setPrice(new BigDecimal("99.99"));
        response.setQuantity(100);
        response.setUserId("seller-1");
        response.setCategoryId("cat-1");
        response.setImages(Arrays.asList("image1.jpg", "image2.jpg"));

        assertEquals("prod-1", response.getId());
        assertEquals("Test Product", response.getName());
        assertEquals("A test product", response.getDescription());
        assertEquals(new BigDecimal("99.99"), response.getPrice());
        assertEquals(100, response.getQuantity());
        assertEquals("seller-1", response.getUserId());
        assertEquals("cat-1", response.getCategoryId());
        assertEquals(2, response.getImages().size());
    }

    @Test
    void testProductResponseSetters() {
        ProductResponse response = new ProductResponse();
        response.setId("prod-2");
        response.setName("Another Product");
        response.setDescription("Another description");
        response.setPrice(new BigDecimal("150.00"));
        response.setQuantity(200);
        response.setUserId("seller-2");
        response.setCategoryId("cat-2");
        response.setImages(Arrays.asList("img.jpg"));

        assertEquals("prod-2", response.getId());
        assertEquals("Another Product", response.getName());
        assertEquals(new BigDecimal("150.00"), response.getPrice());
        assertEquals(200, response.getQuantity());
    }

    @Test
    void testProductResponseEqualsAndHashCode() {
        ProductResponse response1 = new ProductResponse();
        response1.setId("prod-1");
        response1.setName("Test");
        response1.setPrice(new BigDecimal("10.00"));

        ProductResponse response2 = new ProductResponse();
        response2.setId("prod-1");
        response2.setName("Test");
        response2.setPrice(new BigDecimal("10.00"));

        ProductResponse response3 = new ProductResponse();
        response3.setId("prod-2");
        response3.setName("Different");

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testProductResponseToString() {
        ProductResponse response = new ProductResponse();
        response.setId("prod-1");
        response.setName("Test Product");
        response.setPrice(new BigDecimal("99.99"));

        String toString = response.toString();
        assertTrue(toString.contains("prod-1"));
        assertTrue(toString.contains("Test Product"));
    }

    @Test
    void testProductResponseWithEmptyImages() {
        ProductResponse response = new ProductResponse();
        response.setId("prod-1");
        response.setImages(Arrays.asList());

        assertNotNull(response.getImages());
        assertTrue(response.getImages().isEmpty());
    }

    @Test
    void testProductResponseWithNullFields() {
        ProductResponse response = new ProductResponse();

        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getDescription());
        assertNull(response.getPrice());
        assertNull(response.getQuantity());
        assertNull(response.getUserId());
        assertNull(response.getCategoryId());
        assertNull(response.getImages());
    }
}
