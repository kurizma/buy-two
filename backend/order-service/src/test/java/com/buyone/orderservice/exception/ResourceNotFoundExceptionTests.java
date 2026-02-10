package com.buyone.orderservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTests {

    @Test
    void testResourceNotFoundExceptionNoArgs() {
        ResourceNotFoundException ex = new ResourceNotFoundException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testResourceNotFoundExceptionWithMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        assertEquals("Resource not found", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testResourceNotFoundExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found", cause);
        
        assertEquals("Not found", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void testResourceNotFoundExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        ResourceNotFoundException ex = new ResourceNotFoundException(cause);
        
        assertEquals(cause, ex.getCause());
        assertTrue(ex.getMessage().contains("Root cause"));
    }

    @Test
    void testResourceNotFoundExceptionIsRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Test");
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void testResourceNotFoundExceptionThrowAndCatch() {
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Test exception");
        });
    }
}
