package com.buyone.productservice.response;

import org.junit.jupiter.api.Test;
import java.util.List;
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
        ApiResponse<String> response = new ApiResponse<>(true, "Success message", "data");
        
        assertTrue(response.isSuccess());
        assertEquals("Success message", response.getMessage());
        assertEquals("data", response.getData());
    }

    @Test
    void testBuilder() {
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .success(true)
                .message("Count retrieved")
                .data(42)
                .build();
        
        assertTrue(response.isSuccess());
        assertEquals("Count retrieved", response.getMessage());
        assertEquals(42, response.getData());
    }

    @Test
    void testSettersAndGetters() {
        ApiResponse<String> response = new ApiResponse<>();
        
        response.setSuccess(true);
        response.setMessage("Updated");
        response.setData("value");
        
        assertTrue(response.isSuccess());
        assertEquals("Updated", response.getMessage());
        assertEquals("value", response.getData());
    }

    @Test
    void testGenericTypeWithList() {
        List<String> items = List.of("item1", "item2", "item3");
        ApiResponse<List<String>> response = new ApiResponse<>(true, "Items fetched", items);
        
        assertTrue(response.isSuccess());
        assertEquals(3, response.getData().size());
    }

    @Test
    void testGenericTypeWithObject() {
        ProductResponse product = ProductResponse.builder().id("p1").name("Test").build();
        ApiResponse<ProductResponse> response = new ApiResponse<>(true, "Product found", product);
        
        assertTrue(response.isSuccess());
        assertEquals("p1", response.getData().getId());
    }

    @Test
    void testErrorResponse() {
        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message("Error occurred")
                .data(null)
                .build();
        
        assertFalse(response.isSuccess());
        assertEquals("Error occurred", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testEqualsAndHashCode() {
        ApiResponse<String> resp1 = new ApiResponse<>(true, "msg", "data");
        ApiResponse<String> resp2 = new ApiResponse<>(true, "msg", "data");
        ApiResponse<String> resp3 = new ApiResponse<>(false, "other", "diff");
        
        assertEquals(resp1, resp2);
        assertEquals(resp1.hashCode(), resp2.hashCode());
        assertNotEquals(resp1, resp3);
    }

    @Test
    void testToString() {
        ApiResponse<String> response = new ApiResponse<>(true, "Test", "value");
        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("Test"));
        assertTrue(str.contains("value"));
    }
}
