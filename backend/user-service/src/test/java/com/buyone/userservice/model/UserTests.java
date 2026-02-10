package com.buyone.userservice.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTests {

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getRole());
        assertNull(user.getAvatar());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User("1", "John Doe", "john@example.com", "password123", Role.CLIENT, "avatar.png");
        
        assertEquals("1", user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Role.CLIENT, user.getRole());
        assertEquals("avatar.png", user.getAvatar());
    }

    @Test
    void testBuilder() {
        User user = User.builder()
                .id("1")
                .name("Jane Doe")
                .email("jane@example.com")
                .password("securePass")
                .role(Role.SELLER)
                .avatar("jane-avatar.png")
                .build();
        
        assertEquals("1", user.getId());
        assertEquals("Jane Doe", user.getName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("securePass", user.getPassword());
        assertEquals(Role.SELLER, user.getRole());
        assertEquals("jane-avatar.png", user.getAvatar());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        
        user.setId("2");
        user.setName("Bob Smith");
        user.setEmail("bob@example.com");
        user.setPassword("bobPass123");
        user.setRole(Role.CLIENT);
        user.setAvatar("bob-avatar.jpg");
        
        assertEquals("2", user.getId());
        assertEquals("Bob Smith", user.getName());
        assertEquals("bob@example.com", user.getEmail());
        assertEquals("bobPass123", user.getPassword());
        assertEquals(Role.CLIENT, user.getRole());
        assertEquals("bob-avatar.jpg", user.getAvatar());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder()
                .id("1")
                .name("John")
                .email("john@test.com")
                .password("pass")
                .role(Role.CLIENT)
                .avatar("avatar.png")
                .build();
        
        User user2 = User.builder()
                .id("1")
                .name("John")
                .email("john@test.com")
                .password("pass")
                .role(Role.CLIENT)
                .avatar("avatar.png")
                .build();
        
        User user3 = User.builder()
                .id("2")
                .name("Jane")
                .email("jane@test.com")
                .password("pass2")
                .role(Role.SELLER)
                .build();
        
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
    }

    @Test
    void testToString() {
        User user = User.builder()
                .id("1")
                .name("Test User")
                .email("test@example.com")
                .password("testPass")
                .role(Role.CLIENT)
                .avatar("test.png")
                .build();
        
        String str = user.toString();
        assertNotNull(str);
        assertTrue(str.contains("1"));
        assertTrue(str.contains("Test User"));
        assertTrue(str.contains("test@example.com"));
    }

    @Test
    void testNullAvatar() {
        User user = User.builder()
                .id("1")
                .name("No Avatar User")
                .email("noavatar@test.com")
                .password("pass")
                .role(Role.CLIENT)
                .build();
        
        assertNull(user.getAvatar());
    }

    @Test
    void testEqualsWithNull() {
        User user = new User();
        assertNotEquals(null, user);
    }

    @Test
    void testEqualsWithDifferentClass() {
        User user = new User();
        assertNotEquals("string", user);
    }
}
