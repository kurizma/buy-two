package com.buyone.orderservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConflictExceptionTests {

    @Test
    void testConflictExceptionNoArgs() {
        ConflictException ex = new ConflictException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testConflictExceptionWithMessage() {
        ConflictException ex = new ConflictException("Conflict message");
        assertEquals("Conflict message", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testConflictExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        ConflictException ex = new ConflictException("Conflict occurred", cause);
        
        assertEquals("Conflict occurred", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void testConflictExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        ConflictException ex = new ConflictException(cause);
        
        assertEquals(cause, ex.getCause());
        assertTrue(ex.getMessage().contains("Root cause"));
    }

    @Test
    void testConflictExceptionIsRuntimeException() {
        ConflictException ex = new ConflictException("Test");
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void testConflictExceptionThrowAndCatch() {
        assertThrows(ConflictException.class, () -> {
            throw new ConflictException("Test exception");
        });
    }
}
