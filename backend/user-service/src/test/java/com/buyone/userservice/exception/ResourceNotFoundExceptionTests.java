package com.buyone.userservice.exception;

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
        ResourceNotFoundException exception = new ResourceNotFoundException("User not found");
        assertEquals("User not found", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        Throwable cause = new RuntimeException("Root cause");
        ResourceNotFoundException exception = new ResourceNotFoundException("Not found", cause);
        
        assertEquals("Not found", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new RuntimeException("Original error");
        ResourceNotFoundException exception = new ResourceNotFoundException(cause);
        
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Original error"));
    }

    @Test
    void testIsRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException();
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowAndCatch() {
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Test exception");
        });
    }

    @Test
    void testExceptionChaining() {
        Exception original = new IllegalArgumentException("Original");
        ResourceNotFoundException wrapper = new ResourceNotFoundException("Wrapped", original);
        
        assertEquals("Wrapped", wrapper.getMessage());
        assertEquals(original, wrapper.getCause());
        assertEquals("Original", wrapper.getCause().getMessage());
    }

    @Test
    void testNullMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException((String) null);
        assertNull(exception.getMessage());
    }

    @Test
    void testNullCause() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Message", null);
        assertEquals("Message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testEmptyMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("");
        assertEquals("", exception.getMessage());
    }
}
