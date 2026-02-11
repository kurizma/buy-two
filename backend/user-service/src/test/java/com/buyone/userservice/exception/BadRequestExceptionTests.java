package com.buyone.userservice.exception;

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

    @Test
    void testExceptionChaining() {
        Exception original = new IllegalArgumentException("Original");
        BadRequestException wrapper = new BadRequestException("Wrapped", original);
        
        assertEquals("Wrapped", wrapper.getMessage());
        assertEquals(original, wrapper.getCause());
        assertEquals("Original", wrapper.getCause().getMessage());
    }

    @Test
    void testNullMessage() {
        BadRequestException exception = new BadRequestException((String) null);
        assertNull(exception.getMessage());
    }

    @Test
    void testNullCause() {
        BadRequestException exception = new BadRequestException("Message", null);
        assertEquals("Message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testEmptyMessage() {
        BadRequestException exception = new BadRequestException("");
        assertEquals("", exception.getMessage());
    }
}
