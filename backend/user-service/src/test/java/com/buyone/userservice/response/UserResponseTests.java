package com.buyone.userservice.response;

import com.buyone.userservice.model.Role;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserResponseTests {

    @Test
    void testNoArgsConstructor() {
        UserResponse response = new UserResponse();
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getEmail());
        assertNull(response.getRole());
        assertNull(response.getAvatar());
    }

    @Test
    void testAllArgsConstructor() {
        UserResponse response = new UserResponse("1", "John Doe", "john@example.com", Role.CLIENT, "avatar.png");
        
        assertEquals("1", response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(Role.CLIENT, response.getRole());
        assertEquals("avatar.png", response.getAvatar());
    }

    @Test
    void testBuilder() {
        UserResponse response = UserResponse.builder()
                .id("2")
                .name("Jane Doe")
                .email("jane@example.com")
                .role(Role.SELLER)
                .avatar("jane-avatar.png")
                .build();
        
        assertEquals("2", response.getId());
        assertEquals("Jane Doe", response.getName());
        assertEquals("jane@example.com", response.getEmail());
        assertEquals(Role.SELLER, response.getRole());
        assertEquals("jane-avatar.png", response.getAvatar());
    }

    @Test
    void testSettersAndGetters() {
        UserResponse response = new UserResponse();
        response.setId("3");
        response.setName("Bob Smith");
        response.setEmail("bob@example.com");
        response.setRole(Role.CLIENT);
        response.setAvatar("bob.jpg");
        
        assertEquals("3", response.getId());
        assertEquals("Bob Smith", response.getName());
        assertEquals("bob@example.com", response.getEmail());
        assertEquals(Role.CLIENT, response.getRole());
        assertEquals("bob.jpg", response.getAvatar());
    }

    @Test
    void testEqualsAndHashCode() {
        UserResponse response1 = new UserResponse("1", "John", "john@test.com", Role.CLIENT, "avatar.png");
        UserResponse response2 = new UserResponse("1", "John", "john@test.com", Role.CLIENT, "avatar.png");
        UserResponse response3 = new UserResponse("2", "Jane", "jane@test.com", Role.SELLER, null);
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        UserResponse response = UserResponse.builder()
                .id("1")
                .name("Test User")
                .email("test@example.com")
                .role(Role.CLIENT)
                .build();
        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("Test User"));
        assertTrue(str.contains("test@example.com"));
    }

    @Test
    void testNullAvatar() {
        UserResponse response = UserResponse.builder()
                .id("1")
                .name("No Avatar")
                .email("noavatar@test.com")
                .role(Role.CLIENT)
                .build();
        
        assertNull(response.getAvatar());
    }

    @Test
    void testClientRole() {
        UserResponse response = UserResponse.builder()
                .role(Role.CLIENT)
                .build();
        assertEquals(Role.CLIENT, response.getRole());
    }

    @Test
    void testSellerRole() {
        UserResponse response = UserResponse.builder()
                .role(Role.SELLER)
                .build();
        assertEquals(Role.SELLER, response.getRole());
    }

    @Test
    void testEqualsWithNull() {
        UserResponse response = new UserResponse();
        assertNotEquals(null, response);
    }

    @Test
    void testEqualsWithDifferentClass() {
        UserResponse response = new UserResponse();
        assertNotEquals("string", response);
    }

    @Test
    void testDifferentIds() {
        UserResponse response1 = UserResponse.builder().id("1").build();
        UserResponse response2 = UserResponse.builder().id("2").build();
        assertNotEquals(response1, response2);
    }

    @Test
    void testDifferentEmails() {
        UserResponse response1 = UserResponse.builder().email("a@test.com").build();
        UserResponse response2 = UserResponse.builder().email("b@test.com").build();
        assertNotEquals(response1, response2);
    }

    @Test
    void testDifferentRoles() {
        UserResponse response1 = UserResponse.builder().role(Role.CLIENT).build();
        UserResponse response2 = UserResponse.builder().role(Role.SELLER).build();
        assertNotEquals(response1, response2);
    }
}
