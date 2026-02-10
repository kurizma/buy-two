package com.buyone.productservice.exception;

import com.buyone.productservice.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTests {

    @InjectMocks
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
        assertNotNull(response.getBody());
        assertEquals("400", response.getBody().code());
        assertEquals("Invalid input", response.getBody().message());
    }

    @Test
    void testHandleForbidden() {
        ForbiddenException ex = new ForbiddenException("Access denied");
        
        ResponseEntity<ErrorResponse> response = handler.handleForbidden(ex);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("403", response.getBody().code());
        assertEquals("Access denied", response.getBody().message());
    }

    @Test
    void testHandleProductNotFound() {
        ProductNotFoundException ex = new ProductNotFoundException("Product not found");
        
        ResponseEntity<ErrorResponse> response = handler.handleProductNotFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("404", response.getBody().code());
        assertEquals("Product not found", response.getBody().message());
    }

    @Test
    void testHandleConflict() {
        ConflictException ex = new ConflictException("Resource already exists");
        
        ResponseEntity<ErrorResponse> response = handler.handleConflict(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("409", response.getBody().code());
        assertEquals("Resource already exists", response.getBody().message());
    }

    @Test
    void testHandleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("Not authorized");
        
        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("403", response.getBody().code());
        assertEquals("Access denied", response.getBody().message());
    }

    @Test
    void testHandleMethodNotAllowed() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
        
        ResponseEntity<ErrorResponse> response = handler.handleMethodNotAllowed(ex);
        
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("405", response.getBody().code());
        assertEquals("Method not allowed", response.getBody().message());
    }

    @Test
    void testHandleValidation() {
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
    }

    @Test
    void testHandleValidationWithMultipleErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "name", "Name is required");
        FieldError fieldError2 = new FieldError("object", "price", "Price must be positive");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().details());
    }

    @Test
    void testHandleMissingParams() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("productId", "String");
        
        ResponseEntity<ErrorResponse> response = handler.handleMissingParams(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("400", response.getBody().code());
        assertTrue(response.getBody().message().contains("productId"));
    }

    @Test
    void testHandleConstraintViolation() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("name");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");
        violations.add(violation);
        
        ConstraintViolationException ex = new ConstraintViolationException(violations);
        
        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().message());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected error");
        
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("500", response.getBody().code());
    }

    @Test
    void testHandleGenericExceptionWithNullMessage() {
        Exception ex = new RuntimeException();
        
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
