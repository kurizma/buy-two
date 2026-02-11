package com.buyone.mediaservice.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MediaOwnerTypeTests {

    @Test
    void testUserType() {
        MediaOwnerType type = MediaOwnerType.USER;
        assertEquals("USER", type.name());
    }

    @Test
    void testProductType() {
        MediaOwnerType type = MediaOwnerType.PRODUCT;
        assertEquals("PRODUCT", type.name());
    }

    @Test
    void testValues() {
        MediaOwnerType[] types = MediaOwnerType.values();
        assertEquals(2, types.length);
        assertEquals(MediaOwnerType.USER, types[0]);
        assertEquals(MediaOwnerType.PRODUCT, types[1]);
    }

    @Test
    void testValueOf() {
        assertEquals(MediaOwnerType.USER, MediaOwnerType.valueOf("USER"));
        assertEquals(MediaOwnerType.PRODUCT, MediaOwnerType.valueOf("PRODUCT"));
    }

    @Test
    void testOrdinal() {
        assertEquals(0, MediaOwnerType.USER.ordinal());
        assertEquals(1, MediaOwnerType.PRODUCT.ordinal());
    }

    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> MediaOwnerType.valueOf("INVALID"));
    }

    @Test
    void testToString() {
        assertEquals("USER", MediaOwnerType.USER.toString());
        assertEquals("PRODUCT", MediaOwnerType.PRODUCT.toString());
    }
}
