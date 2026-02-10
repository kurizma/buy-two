package com.buyone.mediaservice.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConflictExceptionTests {

    @Test
    void testDefaultConstructor() {
        ConflictException exception = new ConflictException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        ConflictException exception = new ConflictException("Resource already exists");
        assertEquals("Resource already exists", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        Throwable cause = new RuntimeException("Root cause");
        ConflictException exception = new ConflictException("Conflict detected", cause);
        
        assertEquals("Conflict detected", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new RuntimeException("Original error");
        ConflictException exception = new ConflictException(cause);
        
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Original error"));
    }

    @Test
    void testIsRuntimeException() {
        ConflictException exception = new ConflictException();
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowAndCatch() {
        assertThrows(ConflictException.class, () -> {
            throw new ConflictException("Test exception");
        });
    }
}
