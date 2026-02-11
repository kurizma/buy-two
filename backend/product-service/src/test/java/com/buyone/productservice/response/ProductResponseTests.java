package com.buyone.productservice.response;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProductResponseTests {

    @Test
    void testNoArgsConstructor() {
        ProductResponse response = new ProductResponse();
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getDescription());
        assertNull(response.getPrice());
        assertNull(response.getQuantity());
        assertNull(response.getUserId());
        assertNull(response.getCategoryId());
        assertNull(response.getImages());
    }

    @Test
    void testAllArgsConstructor() {
        List<String> images = List.of("img1.jpg", "img2.jpg");
        ProductResponse response = new ProductResponse("id1", "Product", "Description",
                new BigDecimal("99.99"), 10, "user1", "cat1", images);
        
        assertEquals("id1", response.getId());
        assertEquals("Product", response.getName());
        assertEquals("Description", response.getDescription());
        assertEquals(new BigDecimal("99.99"), response.getPrice());
        assertEquals(10, response.getQuantity());
        assertEquals("user1", response.getUserId());
        assertEquals("cat1", response.getCategoryId());
        assertEquals(2, response.getImages().size());
    }

    @Test
    void testBuilder() {
        ProductResponse response = ProductResponse.builder()
                .id("p1")
                .name("Builder Product")
                .description("Built via builder")
                .price(new BigDecimal("49.99"))
                .quantity(5)
                .userId("u1")
                .categoryId("c1")
                .images(List.of("test.png"))
                .build();
        
        assertEquals("p1", response.getId());
        assertEquals("Builder Product", response.getName());
        assertEquals(new BigDecimal("49.99"), response.getPrice());
        assertEquals(5, response.getQuantity());
    }

    @Test
    void testSettersAndGetters() {
        ProductResponse response = new ProductResponse();
        
        response.setId("id2");
        response.setName("Updated Product");
        response.setDescription("Updated Desc");
        response.setPrice(new BigDecimal("199.99"));
        response.setQuantity(20);
        response.setUserId("user2");
        response.setCategoryId("cat2");
        response.setImages(List.of("new.jpg"));
        
        assertEquals("id2", response.getId());
        assertEquals("Updated Product", response.getName());
        assertEquals("Updated Desc", response.getDescription());
        assertEquals(new BigDecimal("199.99"), response.getPrice());
        assertEquals(20, response.getQuantity());
        assertEquals("user2", response.getUserId());
        assertEquals("cat2", response.getCategoryId());
        assertEquals(1, response.getImages().size());
    }

    @Test
    void testEmptyImages() {
        ProductResponse response = ProductResponse.builder()
                .id("p1")
                .images(List.of())
                .build();
        
        assertNotNull(response.getImages());
        assertTrue(response.getImages().isEmpty());
    }

    @Test
    void testNullImages() {
        ProductResponse response = ProductResponse.builder()
                .id("p1")
                .images(null)
                .build();
        
        assertNull(response.getImages());
    }

    @Test
    void testEqualsAndHashCode() {
        ProductResponse resp1 = new ProductResponse("id", "N", "D", new BigDecimal("10"), 1, "u", "c", List.of());
        ProductResponse resp2 = new ProductResponse("id", "N", "D", new BigDecimal("10"), 1, "u", "c", List.of());
        ProductResponse resp3 = new ProductResponse("other", "X", "Y", new BigDecimal("20"), 2, "v", "d", List.of());
        
        assertEquals(resp1, resp2);
        assertEquals(resp1.hashCode(), resp2.hashCode());
        assertNotEquals(resp1, resp3);
    }

    @Test
    void testToString() {
        ProductResponse response = ProductResponse.builder()
                .id("p1")
                .name("Test Product")
                .build();
        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("p1"));
        assertTrue(str.contains("Test Product"));
    }

    @Test
    void testPriceFormats() {
        ProductResponse response1 = ProductResponse.builder().price(new BigDecimal("10.00")).build();
        ProductResponse response2 = ProductResponse.builder().price(new BigDecimal("10.99")).build();
        ProductResponse response3 = ProductResponse.builder().price(new BigDecimal("9999.99")).build();
        
        assertEquals(new BigDecimal("10.00"), response1.getPrice());
        assertEquals(new BigDecimal("10.99"), response2.getPrice());
        assertEquals(new BigDecimal("9999.99"), response3.getPrice());
    }
}
