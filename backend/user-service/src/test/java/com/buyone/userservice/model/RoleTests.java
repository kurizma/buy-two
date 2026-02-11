package com.buyone.userservice.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleTests {

    @Test
    void testClientRole() {
        Role role = Role.CLIENT;
        assertEquals("CLIENT", role.name());
        assertEquals("CLIENT", role.toValue());
    }

    @Test
    void testSellerRole() {
        Role role = Role.SELLER;
        assertEquals("SELLER", role.name());
        assertEquals("SELLER", role.toValue());
    }

    @Test
    void testFromStringClient() {
        Role role = Role.fromString("CLIENT");
        assertEquals(Role.CLIENT, role);
    }

    @Test
    void testFromStringSeller() {
        Role role = Role.fromString("SELLER");
        assertEquals(Role.SELLER, role);
    }

    @Test
    void testFromStringLowerCase() {
        Role role = Role.fromString("client");
        assertEquals(Role.CLIENT, role);
    }

    @Test
    void testFromStringMixedCase() {
        Role role = Role.fromString("ClIeNt");
        assertEquals(Role.CLIENT, role);
    }

    @Test
    void testFromStringWithWhitespace() {
        Role role = Role.fromString("  SELLER  ");
        assertEquals(Role.SELLER, role);
    }

    @Test
    void testFromStringNull() {
        Role role = Role.fromString(null);
        assertNull(role);
    }

    @Test
    void testFromStringInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Role.fromString("INVALID"));
    }

    @Test
    void testToValue() {
        assertEquals("CLIENT", Role.CLIENT.toValue());
        assertEquals("SELLER", Role.SELLER.toValue());
    }

    @Test
    void testValues() {
        Role[] roles = Role.values();
        assertEquals(2, roles.length);
        assertEquals(Role.CLIENT, roles[0]);
        assertEquals(Role.SELLER, roles[1]);
    }

    @Test
    void testValueOf() {
        assertEquals(Role.CLIENT, Role.valueOf("CLIENT"));
        assertEquals(Role.SELLER, Role.valueOf("SELLER"));
    }

    @Test
    void testOrdinal() {
        assertEquals(0, Role.CLIENT.ordinal());
        assertEquals(1, Role.SELLER.ordinal());
    }
}
