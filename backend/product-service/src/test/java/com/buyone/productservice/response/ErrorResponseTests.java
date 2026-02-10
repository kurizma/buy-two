package com.buyone.productservice.response;

import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTests {

    @Test
    void testRecordConstruction() {
        ErrorResponse response = new ErrorResponse("ERR001", "An error occurred", null);
        
        assertNotNull(response);
        assertEquals("ERR001", response.code());
        assertEquals("An error occurred", response.message());
        assertNull(response.details());
    }

    @Test
    void testWithStringDetails() {
        ErrorResponse response = new ErrorResponse("ERR002", "Validation failed", "Field is required");
        
        assertEquals("ERR002", response.code());
        assertEquals("Validation failed", response.message());
        assertEquals("Field is required", response.details());
    }

    @Test
    void testWithMapDetails() {
        Map<String, String> details = Map.of("field", "name", "error", "required");
        ErrorResponse response = new ErrorResponse("ERR003", "Validation error", details);
        
        assertNotNull(response.details());
        assertTrue(response.details() instanceof Map);
    }

    @Test
    void testWithListDetails() {
        List<String> errors = List.of("Name is required", "Price must be positive");
        ErrorResponse response = new ErrorResponse("ERR004", "Multiple validation errors", errors);
        
        assertNotNull(response.details());
        assertTrue(response.details() instanceof List);
    }

    @Test
    void testAllNullValues() {
        ErrorResponse response = new ErrorResponse(null, null, null);
        
        assertNotNull(response);
        assertNull(response.code());
        assertNull(response.message());
        assertNull(response.details());
    }

    @Test
    void testEquality() {
        ErrorResponse resp1 = new ErrorResponse("ERR", "Message", "details");
        ErrorResponse resp2 = new ErrorResponse("ERR", "Message", "details");
        ErrorResponse resp3 = new ErrorResponse("ERR2", "Different", "other");
        
        assertEquals(resp1, resp2);
        assertNotEquals(resp1, resp3);
    }

    @Test
    void testHashCode() {
        ErrorResponse resp1 = new ErrorResponse("ERR", "Message", "details");
        ErrorResponse resp2 = new ErrorResponse("ERR", "Message", "details");
        
        assertEquals(resp1.hashCode(), resp2.hashCode());
    }

    @Test
    void testToString() {
        ErrorResponse response = new ErrorResponse("E001", "Test error", "test detail");
        String str = response.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("E001"));
        assertTrue(str.contains("Test error"));
    }

    @Test
    void testCommonErrorCodes() {
        ErrorResponse notFound = new ErrorResponse("NOT_FOUND", "Resource not found", null);
        ErrorResponse badRequest = new ErrorResponse("BAD_REQUEST", "Invalid input", null);
        ErrorResponse conflict = new ErrorResponse("CONFLICT", "Resource already exists", null);
        ErrorResponse forbidden = new ErrorResponse("FORBIDDEN", "Access denied", null);
        
        assertNotEquals(notFound, badRequest);
        assertNotEquals(badRequest, conflict);
        assertNotEquals(conflict, forbidden);
    }
}
