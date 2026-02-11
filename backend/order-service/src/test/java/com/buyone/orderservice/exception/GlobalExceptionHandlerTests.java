package com.buyone.orderservice.exception;

import com.buyone.orderservice.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) response.getBody().details();
        assertEquals(2, details.size());
    }

    @Test
    void testHandleMissingParams() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("userId", "String");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParams(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().message().contains("userId"));
    }

    @Test
    void testHandleConstraintViolation() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must be a valid email");
        violations.add(violation);

        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().message());
    }

    @Test
    void testHandleBadRequest() {
        BadRequestException ex = new BadRequestException("Invalid input");

        ResponseEntity<ErrorResponse> response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input", response.getBody().message());
    }

    @Test
    void testHandleForbidden() {
        ForbiddenException ex = new ForbiddenException("Access denied");

        ResponseEntity<ErrorResponse> response = handler.handleForbidden(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody().message());
    }

    @Test
    void testHandleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody().message());
    }

    @Test
    void testHandleMethodNotAllowed() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");

        ResponseEntity<ErrorResponse> response = handler.handleMethodNotAllowed(ex);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals("Method not allowed", response.getBody().message());
    }

    @Test
    void testHandleConflict() {
        ConflictException ex = new ConflictException("Resource already exists");

        ResponseEntity<ErrorResponse> response = handler.handleConflict(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Resource already exists", response.getBody().message());
    }

    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody().message());
    }

    @Test
    void testHandleIllegalState() {
        IllegalStateException ex = new IllegalStateException("Invalid state");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalState(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Invalid state", response.getBody().message());
    }

    @Test
    void testHandleGeneric() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody().message());
    }

    @Test
    void testHandleGeneric_NullPointerException() {
        NullPointerException ex = new NullPointerException();

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
