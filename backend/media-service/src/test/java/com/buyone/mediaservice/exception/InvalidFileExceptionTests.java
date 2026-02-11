package com.buyone.mediaservice.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidFileExceptionTests {

    @Test
    void testDefaultConstructor() {
        InvalidFileException exception = new InvalidFileException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        InvalidFileException exception = new InvalidFileException("Invalid file type");
        assertEquals("Invalid file type", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        Throwable cause = new RuntimeException("Root cause");
        InvalidFileException exception = new InvalidFileException("Invalid file", cause);
        
        assertEquals("Invalid file", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new RuntimeException("Original error");
        InvalidFileException exception = new InvalidFileException(cause);
        
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Original error"));
    }

    @Test
    void testIsRuntimeException() {
        InvalidFileException exception = new InvalidFileException();
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowAndCatch() {
        assertThrows(InvalidFileException.class, () -> {
            throw new InvalidFileException("Test exception");
        });
    }

    @Test
    void testFileTypeValidationMessage() {
        InvalidFileException exception = new InvalidFileException("Only JPEG and PNG files are allowed");
        assertTrue(exception.getMessage().contains("JPEG"));
        assertTrue(exception.getMessage().contains("PNG"));
    }
}
