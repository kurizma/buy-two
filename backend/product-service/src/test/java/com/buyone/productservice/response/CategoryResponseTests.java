package com.buyone.productservice.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryResponseTests {

    @Test
    void testRecordConstruction() {
        CategoryResponse response = new CategoryResponse("id1", "electronics", "Electronics", "🔌", "Electronic items");
        
        assertNotNull(response);
        assertEquals("id1", response.id());
        assertEquals("electronics", response.slug());
        assertEquals("Electronics", response.name());
        assertEquals("🔌", response.icon());
        assertEquals("Electronic items", response.description());
    }

    @Test
    void testNullValues() {
        CategoryResponse response = new CategoryResponse("id1", null, null, null, null);
        
        assertNotNull(response);
        assertEquals("id1", response.id());
        assertNull(response.slug());
        assertNull(response.name());
        assertNull(response.icon());
        assertNull(response.description());
    }

    @Test
    void testAllNullValues() {
        CategoryResponse response = new CategoryResponse(null, null, null, null, null);
        assertNotNull(response);
        assertNull(response.id());
    }

    @Test
    void testEquality() {
        CategoryResponse response1 = new CategoryResponse("id", "slug", "name", "icon", "desc");
        CategoryResponse response2 = new CategoryResponse("id", "slug", "name", "icon", "desc");
        CategoryResponse response3 = new CategoryResponse("other", "slug", "name", "icon", "desc");
        
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
    }

    @Test
    void testHashCode() {
        CategoryResponse response1 = new CategoryResponse("id", "slug", "name", "icon", "desc");
        CategoryResponse response2 = new CategoryResponse("id", "slug", "name", "icon", "desc");
        
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        CategoryResponse response = new CategoryResponse("id1", "books", "Books", "📚", "All books");
        String str = response.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("id1"));
        assertTrue(str.contains("books"));
        assertTrue(str.contains("Books"));
    }

    @Test
    void testDifferentCategories() {
        CategoryResponse electronics = new CategoryResponse("e1", "electronics", "Electronics", "🔌", "Gadgets");
        CategoryResponse books = new CategoryResponse("b1", "books", "Books", "📚", "Literature");
        CategoryResponse clothing = new CategoryResponse("c1", "clothing", "Clothing", "👕", "Fashion");
        
        assertNotEquals(electronics, books);
        assertNotEquals(books, clothing);
        assertNotEquals(electronics, clothing);
    }

    @Test
    void testSlugWithSpecialCharacters() {
        CategoryResponse response = new CategoryResponse("id", "home-and-garden", "Home & Garden", "🏠", "Home items");
        assertEquals("home-and-garden", response.slug());
        assertEquals("Home & Garden", response.name());
    }
}
