package com.buyone.discovery_service.exception;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.buyone.discovery_service.response.ErrorResponse;

class GlobalExceptionHandlerTests {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleValidation_SingleFieldError() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "email", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("400", response.getBody().code());
        assertEquals("Validation failed", response.getBody().message());
        
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) response.getBody().details();
        assertEquals("must not be blank", details.get("email"));
    }

    @Test
    void testHandleValidation_MultipleFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "email", "must not be blank");
        FieldError fieldError2 = new FieldError("object", "password", "must be at least 8 characters");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) response.getBody().details();
        assertEquals(2, details.size());
        assertEquals("must not be blank", details.get("email"));
        assertEquals("must be at least 8 characters", details.get("password"));
    }

    @Test
    void testHandleValidation_SameFieldMultipleErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "email", "must not be blank");
        FieldError fieldError2 = new FieldError("object", "email", "must be valid email");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) response.getBody().details();
        String emailError = details.get("email");
        assertTrue(emailError.contains("must not be blank"));
        assertTrue(emailError.contains("must be valid email"));
    }

    @Test
    void testHandleValidation_EmptyFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) response.getBody().details();
        assertTrue(details.isEmpty());
    }

    @Test
    void testHandleGeneric_RuntimeException() {
        RuntimeException ex = new RuntimeException("Something went wrong");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("500", response.getBody().code());
        assertEquals("Something went wrong", response.getBody().message());
        assertNull(response.getBody().details());
    }

    @Test
    void testHandleGeneric_NullPointerException() {
        NullPointerException ex = new NullPointerException("Null value encountered");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Null value encountered", response.getBody().message());
    }

    @Test
    void testHandleGeneric_IllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody().message());
    }

    @Test
    void testHandleGeneric_ExceptionWithNullMessage() {
        Exception ex = new Exception((String) null);

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().message());
    }

    @Test
    void testHandleGeneric_CustomException() {
        Exception ex = new Exception("Custom error message");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Custom error message", response.getBody().message());
        assertNull(response.getBody().details());
    }
}
