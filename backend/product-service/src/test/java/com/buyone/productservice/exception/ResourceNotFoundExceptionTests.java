package com.buyone.productservice.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTests {

    @Test
    void testDefaultConstructor() {
        ResourceNotFoundException exception = new ResourceNotFoundException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        String message = "Resource with id 456 not found";
        Throwable cause = new IllegalArgumentException("root cause");
        ResourceNotFoundException exception = new ResourceNotFoundException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new IllegalArgumentException("root cause");
        ResourceNotFoundException exception = new ResourceNotFoundException(cause);
        
        assertNotNull(exception.getCause());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testIsRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Test not found");
        });
    }

    @Test
    void testExceptionWithResourceTypeAndId() {
        String resourceType = "Category";
        String resourceId = "cat-789";
        ResourceNotFoundException exception = new ResourceNotFoundException(resourceType + " with id " + resourceId + " not found");
        assertTrue(exception.getMessage().contains(resourceType));
        assertTrue(exception.getMessage().contains(resourceId));
    }

    @Test
    void testChainedException() {
        Exception original = new IllegalArgumentException("Invalid resource");
        ResourceNotFoundException exception = new ResourceNotFoundException("Wrapper", original);
        
        assertEquals("Wrapper", exception.getMessage());
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }
}
