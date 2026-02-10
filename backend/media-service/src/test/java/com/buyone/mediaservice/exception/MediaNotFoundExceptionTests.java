package com.buyone.mediaservice.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MediaNotFoundExceptionTests {

    @Test
    void testDefaultConstructor() {
        MediaNotFoundException exception = new MediaNotFoundException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testIdConstructor() {
        MediaNotFoundException exception = new MediaNotFoundException("media123");
        assertEquals("Media not found: media123", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        Throwable cause = new RuntimeException("Root cause");
        MediaNotFoundException exception = new MediaNotFoundException("Custom message", cause);
        
        assertEquals("Custom message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new RuntimeException("Original error");
        MediaNotFoundException exception = new MediaNotFoundException(cause);
        
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Original error"));
    }

    @Test
    void testIsRuntimeException() {
        MediaNotFoundException exception = new MediaNotFoundException();
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowAndCatch() {
        assertThrows(MediaNotFoundException.class, () -> {
            throw new MediaNotFoundException("testId");
        });
    }

    @Test
    void testIdMessageFormat() {
        String mediaId = "abc-123-xyz";
        MediaNotFoundException exception = new MediaNotFoundException(mediaId);
        assertTrue(exception.getMessage().contains(mediaId));
        assertTrue(exception.getMessage().startsWith("Media not found:"));
    }

    @Test
    void testEmptyId() {
        MediaNotFoundException exception = new MediaNotFoundException("");
        assertEquals("Media not found: ", exception.getMessage());
    }
}
