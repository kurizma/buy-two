package com.buyone.productservice.exception;

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
        String message = "Resource already exists";
        ConflictException exception = new ConflictException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        String message = "Duplicate entry";
        Throwable cause = new IllegalStateException("root cause");
        ConflictException exception = new ConflictException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new IllegalStateException("root cause");
        ConflictException exception = new ConflictException(cause);
        
        assertNotNull(exception.getCause());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testIsRuntimeException() {
        ConflictException exception = new ConflictException("test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(ConflictException.class, () -> {
            throw new ConflictException("Test conflict");
        });
    }

    @Test
    void testExceptionWithEmptyMessage() {
        ConflictException exception = new ConflictException("");
        assertEquals("", exception.getMessage());
    }

    @Test
    void testChainedException() {
        Exception original = new IllegalStateException("Duplicate key");
        ConflictException exception = new ConflictException("Wrapper", original);
        
        assertEquals("Wrapper", exception.getMessage());
        assertTrue(exception.getCause() instanceof IllegalStateException);
    }
}
