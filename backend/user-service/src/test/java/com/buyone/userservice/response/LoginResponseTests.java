package com.buyone.userservice.response;

import com.buyone.userservice.model.Role;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTests {

    @Test
    void testNoArgsConstructor() {
        LoginResponse response = new LoginResponse();
        assertNotNull(response);
        assertNull(response.getMessage());
        assertNull(response.getToken());
        assertNull(response.getUser());
    }

    @Test
    void testAllArgsConstructor() {
        UserResponse user = UserResponse.builder()
                .id("1")
                .name("John")
                .email("john@example.com")
                .role(Role.CLIENT)
                .build();
        LoginResponse response = new LoginResponse("Login successful", "jwt-token-123", user);
        
        assertEquals("Login successful", response.getMessage());
        assertEquals("jwt-token-123", response.getToken());
        assertNotNull(response.getUser());
        assertEquals("John", response.getUser().getName());
    }

    @Test
    void testBuilder() {
        UserResponse user = UserResponse.builder()
                .id("2")
                .name("Jane")
                .email("jane@example.com")
                .role(Role.SELLER)
                .build();
        LoginResponse response = LoginResponse.builder()
                .message("Welcome back!")
                .token("token-abc")
                .user(user)
                .build();
        
        assertEquals("Welcome back!", response.getMessage());
        assertEquals("token-abc", response.getToken());
        assertEquals("Jane", response.getUser().getName());
    }

    @Test
    void testSettersAndGetters() {
        LoginResponse response = new LoginResponse();
        UserResponse user = new UserResponse();
        user.setName("Bob");
        
        response.setMessage("Success");
        response.setToken("my-token");
        response.setUser(user);
        
        assertEquals("Success", response.getMessage());
        assertEquals("my-token", response.getToken());
        assertEquals("Bob", response.getUser().getName());
    }

    @Test
    void testEqualsAndHashCode() {
        UserResponse user = UserResponse.builder().id("1").name("Test").build();
        LoginResponse response1 = new LoginResponse("msg", "token", user);
        LoginResponse response2 = new LoginResponse("msg", "token", user);
        LoginResponse response3 = new LoginResponse("different", "token", user);
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        LoginResponse response = LoginResponse.builder()
                .message("Test message")
                .token("test-token")
                .build();
        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("Test message"));
        assertTrue(str.contains("test-token"));
    }

    @Test
    void testNullUser() {
        LoginResponse response = LoginResponse.builder()
                .message("Login failed")
                .token(null)
                .user(null)
                .build();
        
        assertNull(response.getUser());
        assertNull(response.getToken());
    }

    @Test
    void testWithCompleteUserResponse() {
        UserResponse user = UserResponse.builder()
                .id("123")
                .name("Complete User")
                .email("complete@example.com")
                .role(Role.CLIENT)
                .avatar("avatar.png")
                .build();
        
        LoginResponse response = LoginResponse.builder()
                .message("Welcome")
                .token("complete-token")
                .user(user)
                .build();
        
        assertEquals("123", response.getUser().getId());
        assertEquals("Complete User", response.getUser().getName());
        assertEquals("complete@example.com", response.getUser().getEmail());
        assertEquals(Role.CLIENT, response.getUser().getRole());
        assertEquals("avatar.png", response.getUser().getAvatar());
    }
}
