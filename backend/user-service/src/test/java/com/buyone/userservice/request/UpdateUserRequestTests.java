package com.buyone.userservice.request;

import com.buyone.userservice.model.Role;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UpdateUserRequestTests {

    private final Validator validator;

    public UpdateUserRequestTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        UpdateUserRequest request = new UpdateUserRequest();
        assertNotNull(request);
        assertNull(request.getName());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
        assertNull(request.getCurrentPassword());
        assertNull(request.getRole());
        assertNull(request.getAvatar());
    }

    @Test
    void testAllArgsConstructor() {
        UpdateUserRequest request = new UpdateUserRequest("John Doe", "john@example.com", "password123", null, Role.CLIENT, "avatar.png");
        
        assertEquals("John Doe", request.getName());
        assertEquals("john@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertNull(request.getCurrentPassword());
        assertEquals(Role.CLIENT, request.getRole());
        assertEquals("avatar.png", request.getAvatar());
    }

    @Test
    void testBuilder() {
        UpdateUserRequest request = UpdateUserRequest.builder()
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
        UpdateUserRequest request = new UpdateUserRequest();
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
    void testValidRequestAllFields() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Valid User")
                .email("valid@example.com")
                .password("password12")
                .role(Role.CLIENT)
                .avatar("avatar.png")
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidRequestPartialUpdate() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Only Name")
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptyRequest() {
        UpdateUserRequest request = new UpdateUserRequest();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNameTooShort() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("J")
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 2 and 40")));
    }

    @Test
    void testNameTooLong() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("A".repeat(41))
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testInvalidEmailFormat() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("invalidEmail")
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid email")));
    }

    @Test
    void testEmailTooLong() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("a".repeat(90) + "@example.com")
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testPasswordTooShort() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .password("short")
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 8 and 24")));
    }

    @Test
    void testPasswordTooLong() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .password("a".repeat(25))
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidPasswordLength() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .password("validPass1")
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testRoleCanBeSet() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .role(Role.SELLER)
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertEquals(Role.SELLER, request.getRole());
    }

    @Test
    void testAvatarCanBeSet() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .avatar("new-avatar.png")
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertEquals("new-avatar.png", request.getAvatar());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateUserRequest request1 = new UpdateUserRequest("John", "john@test.com", "pass1234", null, Role.CLIENT, "avatar.png");
        UpdateUserRequest request2 = new UpdateUserRequest("John", "john@test.com", "pass1234", null, Role.CLIENT, "avatar.png");
        UpdateUserRequest request3 = new UpdateUserRequest("Jane", "jane@test.com", "pass1234", null, Role.SELLER, null);
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void testToString() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Test")
                .email("test@example.com")
                .build();
        String str = request.toString();
        assertNotNull(str);
        assertTrue(str.contains("Test"));
        assertTrue(str.contains("test@example.com"));
    }

    @Test
    void testNullFieldsAllowed() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .name(null)
                .email(null)
                .password(null)
                .role(null)
                .avatar(null)
                .build();
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
