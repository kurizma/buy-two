package com.buyone.userservice.request;

import com.buyone.userservice.model.Role;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class RegisterUserRequestTests {

    private final Validator validator;

    public RegisterUserRequestTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        RegisterUserRequest request = new RegisterUserRequest();
        assertNotNull(request);
        assertNull(request.getName());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
        assertNull(request.getRole());
        assertNull(request.getAvatar());
    }

    @Test
    void testAllArgsConstructor() {
        RegisterUserRequest request = new RegisterUserRequest("John Doe", "john@example.com", "password123", Role.CLIENT, "avatar.png");
        
        assertEquals("John Doe", request.getName());
        assertEquals("john@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals(Role.CLIENT, request.getRole());
        assertEquals("avatar.png", request.getAvatar());
    }

    @Test
    void testBuilder() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .password("securePass123")
                .role(Role.SELLER)
                .avatar("jane-avatar.png")
                .build();
        
        assertEquals("Jane Doe", request.getName());
        assertEquals("jane@example.com", request.getEmail());
        assertEquals("securePass123", request.getPassword());
        assertEquals(Role.SELLER, request.getRole());
        assertEquals("jane-avatar.png", request.getAvatar());
    }

    @Test
    void testSettersAndGetters() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setName("Bob Smith");
        request.setEmail("bob@example.com");
        request.setPassword("bobPass123");
        request.setRole(Role.CLIENT);
        request.setAvatar("bob.jpg");
        
        assertEquals("Bob Smith", request.getName());
        assertEquals("bob@example.com", request.getEmail());
        assertEquals("bobPass123", request.getPassword());
        assertEquals(Role.CLIENT, request.getRole());
        assertEquals("bob.jpg", request.getAvatar());
    }

    @Test
    void testValidRequest() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Valid User")
                .email("valid@example.com")
                .password("password12")
                .role(Role.CLIENT)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankName() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("")
                .email("test@example.com")
                .password("password12")
                .role(Role.CLIENT)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testNameTooShort() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("J")
                .email("test@example.com")
                .password("password12")
                .role(Role.CLIENT)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 2 and 40")));
    }

    @Test
    void testNameTooLong() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("A".repeat(41))
                .email("test@example.com")
                .password("password12")
                .role(Role.CLIENT)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testBlankEmail() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Test User")
                .email("")
                .password("password12")
                .role(Role.CLIENT)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testInvalidEmailFormat() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Test User")
                .email("invalidEmail")
                .password("password12")
                .role(Role.CLIENT)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testEmailTooLong() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Test User")
                .email("a".repeat(90) + "@example.com")
                .password("password12")
                .role(Role.CLIENT)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testBlankPassword() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("")
                .role(Role.CLIENT)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testPasswordTooShort() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("short")
                .role(Role.CLIENT)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 8 and 24")));
    }

    @Test
    void testPasswordTooLong() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("a".repeat(25))
                .role(Role.CLIENT)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullRole() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password12")
                .role(null)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Role is required")));
    }

    @Test
    void testOptionalAvatar() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password12")
                .role(Role.CLIENT)
                .build();
        Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertNull(request.getAvatar());
    }

    @Test
    void testEqualsAndHashCode() {
        RegisterUserRequest request1 = new RegisterUserRequest("John", "john@test.com", "pass1234", Role.CLIENT, null);
        RegisterUserRequest request2 = new RegisterUserRequest("John", "john@test.com", "pass1234", Role.CLIENT, null);
        RegisterUserRequest request3 = new RegisterUserRequest("Jane", "jane@test.com", "pass1234", Role.SELLER, null);
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void testToString() {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Test")
                .email("test@example.com")
                .password("pass1234")
                .role(Role.CLIENT)
                .build();
        String str = request.toString();
        assertNotNull(str);
        assertTrue(str.contains("Test"));
        assertTrue(str.contains("test@example.com"));
    }
}
