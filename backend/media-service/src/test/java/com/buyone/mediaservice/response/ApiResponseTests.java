package com.buyone.mediaservice.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTests {

    @Test
    void testNoArgsConstructor() {
        ApiResponse<String> response = new ApiResponse<>();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testAllArgsConstructor() {
        ApiResponse<String> response = new ApiResponse<>(true, "Success", "data here");
        
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals("data here", response.getData());
    }

    @Test
    void testBuilder() {
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .success(true)
                .message("Operation completed")
                .data(42)
                .build();
        
        assertTrue(response.isSuccess());
        assertEquals("Operation completed", response.getMessage());
        assertEquals(42, response.getData());
    }

    @Test
    void testSettersAndGetters() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Test message");
        response.setData("Test data");
        
        assertTrue(response.isSuccess());
        assertEquals("Test message", response.getMessage());
        assertEquals("Test data", response.getData());
    }

    @Test
    void testSuccessFalse() {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message("Operation failed")
                .data(null)
                .build();
        
        assertFalse(response.isSuccess());
        assertEquals("Operation failed", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testWithObjectData() {
        MediaResponse mediaData = new MediaResponse("1", "owner", "/url", null);
        ApiResponse<MediaResponse> response = ApiResponse.<MediaResponse>builder()
                .success(true)
                .message("Media uploaded")
                .data(mediaData)
                .build();
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("1", response.getData().id());
    }

    @Test
    void testEqualsAndHashCode() {
        ApiResponse<String> response1 = new ApiResponse<>(true, "msg", "data");
        ApiResponse<String> response2 = new ApiResponse<>(true, "msg", "data");
        ApiResponse<String> response3 = new ApiResponse<>(false, "diff", "other");
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Test")
                .data("content")
                .build();
        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("true") || str.contains("success"));
    }

    @Test
    void testGenericTypesList() {
        java.util.List<String> list = java.util.List.of("a", "b", "c");
        ApiResponse<java.util.List<String>> response = ApiResponse.<java.util.List<String>>builder()
                .success(true)
                .message("List response")
                .data(list)
                .build();
        
        assertEquals(3, response.getData().size());
    }

    @Test
    void testNullData() {
        ApiResponse<Object> response = new ApiResponse<>(true, "No data", null);
        assertTrue(response.isSuccess());
        assertNull(response.getData());
    }
}
