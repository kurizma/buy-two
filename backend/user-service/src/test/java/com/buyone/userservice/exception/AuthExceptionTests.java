package com.buyone.userservice.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthExceptionTests {

    @Test
    void testDefaultConstructor() {
        AuthException exception = new AuthException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        AuthException exception = new AuthException("Invalid credentials");
        assertEquals("Invalid credentials", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        Throwable cause = new RuntimeException("Root cause");
        AuthException exception = new AuthException("Auth failed", cause);
        
        assertEquals("Auth failed", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new RuntimeException("Original error");
        AuthException exception = new AuthException(cause);
        
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Original error"));
    }

    @Test
    void testIsRuntimeException() {
        AuthException exception = new AuthException();
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowAndCatch() {
        assertThrows(AuthException.class, () -> {
            throw new AuthException("Test exception");
        });
    }

    @Test
    void testExceptionChaining() {
        Exception original = new IllegalArgumentException("Original");
        AuthException wrapper = new AuthException("Wrapped", original);
        
        assertEquals("Wrapped", wrapper.getMessage());
        assertEquals(original, wrapper.getCause());
        assertEquals("Original", wrapper.getCause().getMessage());
    }

    @Test
    void testNullMessage() {
        AuthException exception = new AuthException((String) null);
        assertNull(exception.getMessage());
    }

    @Test
    void testNullCause() {
        AuthException exception = new AuthException("Message", null);
        assertEquals("Message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testEmptyMessage() {
        AuthException exception = new AuthException("");
        assertEquals("", exception.getMessage());
    }
}
