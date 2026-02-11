package com.buyone.productservice.exception;

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
        String message = "Access denied";
        ForbiddenException exception = new ForbiddenException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        String message = "Forbidden action";
        Throwable cause = new SecurityException("root cause");
        ForbiddenException exception = new ForbiddenException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new SecurityException("root cause");
        ForbiddenException exception = new ForbiddenException(cause);
        
        assertNotNull(exception.getCause());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testIsRuntimeException() {
        ForbiddenException exception = new ForbiddenException("test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(ForbiddenException.class, () -> {
            throw new ForbiddenException("Test forbidden");
        });
    }

    @Test
    void testExceptionWithEmptyMessage() {
        ForbiddenException exception = new ForbiddenException("");
        assertEquals("", exception.getMessage());
    }

    @Test
    void testChainedException() {
        Exception original = new SecurityException("Unauthorized");
        ForbiddenException exception = new ForbiddenException("Wrapper", original);
        
        assertEquals("Wrapper", exception.getMessage());
        assertTrue(exception.getCause() instanceof SecurityException);
    }
}
