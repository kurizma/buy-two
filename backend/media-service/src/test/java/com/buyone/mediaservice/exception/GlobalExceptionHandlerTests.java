package com.buyone.mediaservice.exception;

import com.buyone.mediaservice.response.ErrorResponse;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
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
    void testHandleInvalidFile() {
        InvalidFileException ex = new InvalidFileException("Only JPEG allowed");
        ResponseEntity<ErrorResponse> response = handler.handleInvalidFile(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().code());
        assertEquals("Only JPEG allowed", response.getBody().message());
    }

    @Test
    void testHandleMaxUploadSize() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(2097152);
        ResponseEntity<ErrorResponse> response = handler.handleMaxUploadSize(ex);
        
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertEquals("413", response.getBody().code());
        assertEquals("File exceeds 2MB size limit", response.getBody().message());
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
    void testHandleMediaNotFound() {
        MediaNotFoundException ex = new MediaNotFoundException("media123");
        ResponseEntity<ErrorResponse> response = handler.handleMediaNotFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404", response.getBody().code());
        assertTrue(response.getBody().message().contains("media123"));
    }

    @Test
    void testHandleMethodNotAllowed() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("DELETE");
        ResponseEntity<ErrorResponse> response = handler.handleMethodNotAllowed(ex);
        
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals("405", response.getBody().code());
        assertEquals("Method not allowed", response.getBody().message());
    }

    @Test
    void testHandleConflict() {
        ConflictException ex = new ConflictException("Media already exists");
        ResponseEntity<ErrorResponse> response = handler.handleConflict(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("409", response.getBody().code());
        assertEquals("Media already exists", response.getBody().message());
    }

    @Test
    void testHandleGeneric() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500", response.getBody().code());
        assertEquals("Unexpected error", response.getBody().message());
    }

    @Test
    void testHandleValidation() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "file", "File is required");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().code());
        assertEquals("Validation failed", response.getBody().message());
        assertNotNull(response.getBody().details());
    }

    @Test
    void testHandleMissingParams() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("ownerId", "String");
        ResponseEntity<ErrorResponse> response = handler.handleMissingParams(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().code());
        assertTrue(response.getBody().message().contains("ownerId"));
    }

    @Test
    void testHandleConstraintViolation() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("ownerId");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");
        violations.add(violation);
        
        ConstraintViolationException ex = new ConstraintViolationException(violations);
        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().message());
        assertNotNull(response.getBody().details());
    }
}
