package com.buyone.productservice.exception;

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
        String message = "Invalid request parameters";
        BadRequestException exception = new BadRequestException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        String message = "Invalid request";
        Throwable cause = new IllegalArgumentException("root cause");
        BadRequestException exception = new BadRequestException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new IllegalArgumentException("root cause");
        BadRequestException exception = new BadRequestException(cause);
        
        assertNotNull(exception.getCause());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testIsRuntimeException() {
        BadRequestException exception = new BadRequestException("test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException("Test error");
        });
    }

    @Test
    void testExceptionWithEmptyMessage() {
        BadRequestException exception = new BadRequestException("");
        assertEquals("", exception.getMessage());
    }

    @Test
    void testChainedException() {
        Exception original = new IllegalArgumentException("Invalid value");
        BadRequestException exception = new BadRequestException("Wrapper", original);
        
        assertEquals("Wrapper", exception.getMessage());
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }
}
