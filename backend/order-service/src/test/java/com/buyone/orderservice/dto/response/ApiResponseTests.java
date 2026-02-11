package com.buyone.orderservice.dto.response;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTests {

    @Test
    void testApiResponseBuilder() {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Operation successful")
                .data("Test data")
                .build();

        assertTrue(response.isSuccess());
        assertEquals("Operation successful", response.getMessage());
        assertEquals("Test data", response.getData());
    }

    @Test
    void testApiResponseNoArgsConstructor() {
        ApiResponse<String> response = new ApiResponse<>();
        
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testApiResponseAllArgsConstructor() {
        ApiResponse<String> response = new ApiResponse<>(true, "Success", "Data");

        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals("Data", response.getData());
    }

    @Test
    void testApiResponseSetters() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Updated message");
        response.setData("Updated data");

        assertTrue(response.isSuccess());
        assertEquals("Updated message", response.getMessage());
        assertEquals("Updated data", response.getData());
    }

    @Test
    void testApiResponseWithComplexType() {
        Map<String, Object> data = Map.of("key1", "value1", "key2", 123);
        
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Success")
                .data(data)
                .build();

        assertNotNull(response.getData());
        assertEquals("value1", response.getData().get("key1"));
        assertEquals(123, response.getData().get("key2"));
    }

    @Test
    void testApiResponseEqualsAndHashCode() {
        ApiResponse<String> response1 = ApiResponse.<String>builder()
                .success(true)
                .message("Test")
                .data("Data")
                .build();

        ApiResponse<String> response2 = ApiResponse.<String>builder()
                .success(true)
                .message("Test")
                .data("Data")
                .build();

        ApiResponse<String> response3 = ApiResponse.<String>builder()
                .success(false)
                .message("Different")
                .build();

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testApiResponseToString() {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Success message")
                .data("Some data")
                .build();

        String toString = response.toString();
        assertTrue(toString.contains("true"));
        assertTrue(toString.contains("Success message"));
    }

    @Test
    void testApiResponseFailure() {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message("Operation failed")
                .data(null)
                .build();

        assertFalse(response.isSuccess());
        assertEquals("Operation failed", response.getMessage());
        assertNull(response.getData());
    }
}
