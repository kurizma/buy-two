package com.buyone.userservice.request;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTests {

    private final Validator validator;

    public LoginRequestTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        LoginRequest request = new LoginRequest();
        assertNotNull(request);
        assertNull(request.getEmail());
        assertNull(request.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testBuilder() {
        LoginRequest request = LoginRequest.builder()
                .email("builder@example.com")
                .password("builderPass")
                .build();
        assertEquals("builder@example.com", request.getEmail());
        assertEquals("builderPass", request.getPassword());
    }

    @Test
    void testSettersAndGetters() {
        LoginRequest request = new LoginRequest();
        request.setEmail("setter@example.com");
        request.setPassword("setterPass");
        
        assertEquals("setter@example.com", request.getEmail());
        assertEquals("setterPass", request.getPassword());
    }

    @Test
    void testValidRequest() {
        LoginRequest request = new LoginRequest("valid@example.com", "validPass");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankEmail() {
        LoginRequest request = new LoginRequest("", "password123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testNullEmail() {
        LoginRequest request = new LoginRequest(null, "password123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testInvalidEmailFormat() {
        LoginRequest request = new LoginRequest("invalidEmail", "password123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid email")));
    }

    @Test
    void testBlankPassword() {
        LoginRequest request = new LoginRequest("test@example.com", "");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void testNullPassword() {
        LoginRequest request = new LoginRequest("test@example.com", null);
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        LoginRequest request1 = new LoginRequest("test@example.com", "pass");
        LoginRequest request2 = new LoginRequest("test@example.com", "pass");
        LoginRequest request3 = new LoginRequest("other@example.com", "pass");
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void testToString() {
        LoginRequest request = new LoginRequest("test@example.com", "password");
        String str = request.toString();
        assertNotNull(str);
        assertTrue(str.contains("test@example.com"));
    }
}
