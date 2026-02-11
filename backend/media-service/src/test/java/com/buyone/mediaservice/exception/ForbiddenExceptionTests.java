package com.buyone.mediaservice.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ForbiddenExceptionTests {

    @Test
    void testDefaultConstructor() {
        ForbiddenException exception = new ForbiddenException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        ForbiddenException exception = new ForbiddenException("Access denied");
        assertEquals("Access denied", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        Throwable cause = new RuntimeException("Root cause");
        ForbiddenException exception = new ForbiddenException("Forbidden", cause);
        
        assertEquals("Forbidden", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new RuntimeException("Original error");
        ForbiddenException exception = new ForbiddenException(cause);
        
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Original error"));
    }

    @Test
    void testIsRuntimeException() {
        ForbiddenException exception = new ForbiddenException();
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowAndCatch() {
        assertThrows(ForbiddenException.class, () -> {
            throw new ForbiddenException("Test exception");
        });
    }
}
