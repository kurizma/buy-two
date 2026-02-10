package com.buyone.userservice.exception;

import com.buyone.userservice.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.security.access.AccessDeniedException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTests {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleBadRequest() {
        BadRequestException ex = new BadRequestException("Invalid input");
        ResponseEntity<ErrorResponse> response = handler.handleBadRequest(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().code());
        assertEquals("Invalid input", response.getBody().message());
        assertNull(response.getBody().details());
    }

    @Test
    void testHandleAuth() {
        AuthException ex = new AuthException("Invalid credentials");
        ResponseEntity<ErrorResponse> response = handler.handleAuth(ex);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("401", response.getBody().code());
        assertEquals("Invalid credentials", response.getBody().message());
    }

    @Test
    void testHandleForbidden() {
        ForbiddenException ex = new ForbiddenException("Access denied");
        ResponseEntity<ErrorResponse> response = handler.handleForbidden(ex);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("403", response.getBody().code());
        assertEquals("Access denied", response.getBody().message());
    }

    @Test
    void testHandleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("No permission");
        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("403", response.getBody().code());
        assertEquals("Access denied", response.getBody().message());
    }

    @Test
    void testHandleNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User not found");
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404", response.getBody().code());
        assertEquals("User not found", response.getBody().message());
    }

    @Test
    void testHandleMethodNotAllowed() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
        ResponseEntity<ErrorResponse> response = handler.handleMethodNotAllowed(ex);
        
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals("405", response.getBody().code());
        assertEquals("Method not allowed", response.getBody().message());
    }

    @Test
    void testHandleConflict() {
        ConflictException ex = new ConflictException("Email already exists");
        ResponseEntity<ErrorResponse> response = handler.handleConflict(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("409", response.getBody().code());
        assertEquals("Email already exists", response.getBody().message());
    }

    @Test
    void testHandleGeneric() {
        Exception ex = new Exception("Something went wrong");
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500", response.getBody().code());
        assertEquals("Something went wrong", response.getBody().message());
    }

    @Test
    void testHandleValidation() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "email", "Invalid email");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().code());
        assertEquals("Validation failed", response.getBody().message());
        assertNotNull(response.getBody().details());
        assertTrue(response.getBody().details() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) response.getBody().details();
        assertEquals("Invalid email", details.get("email"));
    }

    @Test
    void testHandleValidationMultipleErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("object", "email", "Invalid email");
        FieldError error2 = new FieldError("object", "name", "Name is required");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));
        
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) response.getBody().details();
        assertEquals(2, details.size());
    }

    @Test
    void testHandleMissingParams() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("id", "String");
        ResponseEntity<ErrorResponse> response = handler.handleMissingParams(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().code());
        assertTrue(response.getBody().message().contains("id"));
    }

    @Test
    void testHandleConstraintViolation() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must be valid");
        violations.add(violation);
        
        ConstraintViolationException ex = new ConstraintViolationException(violations);
        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().message());
        assertNotNull(response.getBody().details());
    }

    @Test
    void testHandleValidationDuplicateFields() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("object", "email", "Invalid email");
        FieldError error2 = new FieldError("object", "email", "Email already taken");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));
        
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) response.getBody().details();
        assertTrue(details.get("email").contains("Invalid email"));
        assertTrue(details.get("email").contains("Email already taken"));
    }
}
