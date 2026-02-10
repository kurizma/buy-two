package com.buyone.productservice.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTests {

    @Test
    void testNoArgsConstructor() {
        Category category = new Category();
        assertNotNull(category);
        assertNull(category.getId());
        assertNull(category.getSlug());
        assertNull(category.getName());
        assertNull(category.getIcon());
        assertNull(category.getDescription());
    }

    @Test
    void testAllArgsConstructor() {
        Category category = new Category("1", "electronics", "Electronics", "🔌", "Electronic devices");
        
        assertEquals("1", category.getId());
        assertEquals("electronics", category.getSlug());
        assertEquals("Electronics", category.getName());
        assertEquals("🔌", category.getIcon());
        assertEquals("Electronic devices", category.getDescription());
    }

    @Test
    void testBuilder() {
        Category category = Category.builder()
                .id("2")
                .slug("clothing")
                .name("Clothing")
                .icon("👕")
                .description("Clothes and apparel")
                .build();
        
        assertEquals("2", category.getId());
        assertEquals("clothing", category.getSlug());
        assertEquals("Clothing", category.getName());
        assertEquals("👕", category.getIcon());
        assertEquals("Clothes and apparel", category.getDescription());
    }

    @Test
    void testSettersAndGetters() {
        Category category = new Category();
        
        category.setId("3");
        category.setSlug("books");
        category.setName("Books");
        category.setIcon("📚");
        category.setDescription("Books and literature");
        
        assertEquals("3", category.getId());
        assertEquals("books", category.getSlug());
        assertEquals("Books", category.getName());
        assertEquals("📚", category.getIcon());
        assertEquals("Books and literature", category.getDescription());
    }

    @Test
    void testEqualsAndHashCode() {
        Category cat1 = new Category("1", "tech", "Tech", "💻", "Technology");
        Category cat2 = new Category("1", "tech", "Tech", "💻", "Technology");
        Category cat3 = new Category("2", "food", "Food", "🍔", "Food items");
        
        assertEquals(cat1, cat2);
        assertEquals(cat1.hashCode(), cat2.hashCode());
        assertNotEquals(cat1, cat3);
    }

    @Test
    void testToString() {
        Category category = Category.builder()
                .id("1")
                .name("Test Category")
                .slug("test-category")
                .build();
        String str = category.toString();
        assertNotNull(str);
        assertTrue(str.contains("Test Category"));
    }

    @Test
    void testNullIcon() {
        Category category = Category.builder()
                .id("1")
                .name("No Icon")
                .build();
        assertNull(category.getIcon());
    }

    @Test
    void testNullDescription() {
        Category category = Category.builder()
                .id("1")
                .name("No Description")
                .build();
        assertNull(category.getDescription());
    }
}
