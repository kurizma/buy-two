package com.buyone.orderservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForbiddenExceptionTests {

    @Test
    void testForbiddenExceptionNoArgs() {
        ForbiddenException ex = new ForbiddenException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testForbiddenExceptionWithMessage() {
        ForbiddenException ex = new ForbiddenException("Access forbidden");
        assertEquals("Access forbidden", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testForbiddenExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        ForbiddenException ex = new ForbiddenException("Forbidden", cause);
        
        assertEquals("Forbidden", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void testForbiddenExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        ForbiddenException ex = new ForbiddenException(cause);
        
        assertEquals(cause, ex.getCause());
        assertTrue(ex.getMessage().contains("Root cause"));
    }

    @Test
    void testForbiddenExceptionIsRuntimeException() {
        ForbiddenException ex = new ForbiddenException("Test");
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void testForbiddenExceptionThrowAndCatch() {
        assertThrows(ForbiddenException.class, () -> {
            throw new ForbiddenException("Test exception");
        });
    }
}
