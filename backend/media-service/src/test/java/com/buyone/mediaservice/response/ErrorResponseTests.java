package com.buyone.mediaservice.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTests {

    @Test
    void testRecordConstruction() {
        ErrorResponse response = new ErrorResponse("400", "Bad Request", null);
        
        assertEquals("400", response.code());
        assertEquals("Bad Request", response.message());
        assertNull(response.details());
    }

    @Test
    void testRecordWithDetails() {
        Object details = "Additional error details";
        ErrorResponse response = new ErrorResponse("404", "Not Found", details);
        
        assertEquals("404", response.code());
        assertEquals("Not Found", response.message());
        assertEquals("Additional error details", response.details());
    }

    @Test
    void testRecordWithMapDetails() {
        java.util.Map<String, String> details = java.util.Map.of("field", "error message");
        ErrorResponse response = new ErrorResponse("400", "Validation failed", details);
        
        assertEquals("400", response.code());
        assertEquals("Validation failed", response.message());
        assertNotNull(response.details());
        assertTrue(response.details() instanceof java.util.Map);
    }

    @Test
    void testEqualsAndHashCode() {
        ErrorResponse response1 = new ErrorResponse("500", "Server Error", null);
        ErrorResponse response2 = new ErrorResponse("500", "Server Error", null);
        ErrorResponse response3 = new ErrorResponse("400", "Bad Request", null);
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        ErrorResponse response = new ErrorResponse("401", "Unauthorized", null);
        String str = response.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("401"));
        assertTrue(str.contains("Unauthorized"));
    }

    @Test
    void testNullCode() {
        ErrorResponse response = new ErrorResponse(null, "Error", null);
        assertNull(response.code());
    }

    @Test
    void testNullMessage() {
        ErrorResponse response = new ErrorResponse("500", null, null);
        assertNull(response.message());
    }

    @Test
    void testAllNullValues() {
        ErrorResponse response = new ErrorResponse(null, null, null);
        assertNull(response.code());
        assertNull(response.message());
        assertNull(response.details());
    }

    @Test
    void test413Code() {
        ErrorResponse response = new ErrorResponse("413", "Payload Too Large", null);
        assertEquals("413", response.code());
    }
}
