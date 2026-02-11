package com.buyone.orderservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTests {

    @Test
    void testBadRequestExceptionNoArgs() {
        BadRequestException ex = new BadRequestException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testBadRequestExceptionWithMessage() {
        BadRequestException ex = new BadRequestException("Bad request message");
        assertEquals("Bad request message", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testBadRequestExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        BadRequestException ex = new BadRequestException("Bad request", cause);
        
        assertEquals("Bad request", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void testBadRequestExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        BadRequestException ex = new BadRequestException(cause);
        
        assertEquals(cause, ex.getCause());
        assertTrue(ex.getMessage().contains("Root cause"));
    }

    @Test
    void testBadRequestExceptionIsRuntimeException() {
        BadRequestException ex = new BadRequestException("Test");
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void testBadRequestExceptionThrowAndCatch() {
        assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException("Test exception");
        });
    }
}
