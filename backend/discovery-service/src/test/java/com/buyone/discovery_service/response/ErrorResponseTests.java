package com.buyone.discovery_service.response;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ErrorResponseTests {

    @Test
    void testErrorResponseCreation() {
        ErrorResponse response = new ErrorResponse("400", "Bad Request", null);
        
        assertEquals("400", response.code());
        assertEquals("Bad Request", response.message());
        assertNull(response.details());
    }

    @Test
    void testErrorResponseWithDetails() {
        Map<String, String> details = Map.of("field", "error message");
        ErrorResponse response = new ErrorResponse("422", "Validation failed", details);
        
        assertEquals("422", response.code());
        assertEquals("Validation failed", response.message());
        assertNotNull(response.details());
        assertEquals(details, response.details());
    }

    @Test
    void testErrorResponseEquality() {
        ErrorResponse response1 = new ErrorResponse("500", "Internal Error", null);
        ErrorResponse response2 = new ErrorResponse("500", "Internal Error", null);
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testErrorResponseInequality() {
        ErrorResponse response1 = new ErrorResponse("400", "Bad Request", null);
        ErrorResponse response2 = new ErrorResponse("500", "Internal Error", null);
        
        assertNotEquals(response1, response2);
    }

    @Test
    void testErrorResponseToString() {
        ErrorResponse response = new ErrorResponse("404", "Not Found", null);
        String toString = response.toString();
        
        assertTrue(toString.contains("404"));
        assertTrue(toString.contains("Not Found"));
    }

    @Test
    void testErrorResponseWithObjectDetails() {
        Object details = new Object[] {"item1", "item2"};
        ErrorResponse response = new ErrorResponse("400", "Multiple errors", details);
        
        assertNotNull(response.details());
    }

    @Test
    void testErrorResponseWithEmptyDetails() {
        Map<String, String> emptyDetails = Map.of();
        ErrorResponse response = new ErrorResponse("400", "Error", emptyDetails);
        
        assertNotNull(response.details());
        assertTrue(((Map<?, ?>) response.details()).isEmpty());
    }
}
