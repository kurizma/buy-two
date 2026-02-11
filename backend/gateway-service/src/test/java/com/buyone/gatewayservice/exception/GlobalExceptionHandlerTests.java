package com.buyone.gatewayservice.exception;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.buyone.gatewayservice.response.ErrorResponse;

class GlobalExceptionHandlerTests {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleValidationSingleError() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "name", "Name is required");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("400", response.getBody().code());
        assertEquals("Validation failed", response.getBody().message());
        assertNotNull(response.getBody().details());
    }

    @Test
    void testHandleValidationMultipleErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "name", "Name is required");
        FieldError fieldError2 = new FieldError("object", "email", "Email is invalid");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, String> details = (Map<String, String>) response.getBody().details();
        assertEquals(2, details.size());
    }

    @Test
    void testHandleValidationDuplicateFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "name", "Name is too short");
        FieldError fieldError2 = new FieldError("object", "name", "Name is required");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> details = (Map<String, String>) response.getBody().details();
        assertTrue(details.get("name").contains("too short") || details.get("name").contains("required"));
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected error");
        
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("500", response.getBody().code());
        assertEquals("Unexpected error", response.getBody().message());
        assertNull(response.getBody().details());
    }

    @Test
    void testHandleGenericExceptionWithNullMessage() {
        Exception ex = new RuntimeException();
        
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("500", response.getBody().code());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody().message());
    }

    @Test
    void testHandleNullPointerException() {
        NullPointerException ex = new NullPointerException("Null value encountered");
        
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Null value encountered", response.getBody().message());
    }

    @Test
    void testHandleEmptyFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> details = (Map<String, String>) response.getBody().details();
        assertTrue(details.isEmpty());
    }
}
