package com.buyone.mediaservice.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTests {

    @Test
    void testDefaultConstructor() {
        BadRequestException exception = new BadRequestException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        BadRequestException exception = new BadRequestException("Invalid input");
        assertEquals("Invalid input", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        Throwable cause = new RuntimeException("Root cause");
        BadRequestException exception = new BadRequestException("Bad request", cause);
        
        assertEquals("Bad request", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new RuntimeException("Original error");
        BadRequestException exception = new BadRequestException(cause);
        
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Original error"));
    }

    @Test
    void testIsRuntimeException() {
        BadRequestException exception = new BadRequestException();
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowAndCatch() {
        assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException("Test exception");
        });
    }
}
