package com.buyone.productservice.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductNotFoundExceptionTests {

    @Test
    void testDefaultConstructor() {
        ProductNotFoundException exception = new ProductNotFoundException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        String message = "Product not found";
        ProductNotFoundException exception = new ProductNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        String message = "Product with id 123 not found";
        Throwable cause = new IllegalArgumentException("root cause");
        ProductNotFoundException exception = new ProductNotFoundException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new IllegalArgumentException("root cause");
        ProductNotFoundException exception = new ProductNotFoundException(cause);
        
        assertNotNull(exception.getCause());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testIsRuntimeException() {
        ProductNotFoundException exception = new ProductNotFoundException("test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(ProductNotFoundException.class, () -> {
            throw new ProductNotFoundException("Test not found");
        });
    }

    @Test
    void testExceptionWithProductId() {
        String productId = "prod-123";
        ProductNotFoundException exception = new ProductNotFoundException("Product with id " + productId + " not found");
        assertTrue(exception.getMessage().contains(productId));
    }

    @Test
    void testChainedException() {
        Exception original = new IllegalArgumentException("Invalid ID");
        ProductNotFoundException exception = new ProductNotFoundException("Wrapper", original);
        
        assertEquals("Wrapper", exception.getMessage());
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }
}
