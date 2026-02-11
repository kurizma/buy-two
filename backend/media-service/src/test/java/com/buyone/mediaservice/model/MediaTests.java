package com.buyone.mediaservice.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class MediaTests {

    @Test
    void testNoArgsConstructor() {
        Media media = new Media();
        assertNotNull(media);
        assertNull(media.getId());
        assertNull(media.getOwnerId());
        assertNull(media.getOwnerType());
        assertNull(media.getImagePath());
        assertNull(media.getCreatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        Instant now = Instant.now();
        Media media = new Media("1", "user123", MediaOwnerType.USER, "/images/avatar.png", now);
        
        assertEquals("1", media.getId());
        assertEquals("user123", media.getOwnerId());
        assertEquals(MediaOwnerType.USER, media.getOwnerType());
        assertEquals("/images/avatar.png", media.getImagePath());
        assertEquals(now, media.getCreatedAt());
    }

    @Test
    void testBuilder() {
        Instant now = Instant.now();
        Media media = Media.builder()
                .id("2")
                .ownerId("product456")
                .ownerType(MediaOwnerType.PRODUCT)
                .imagePath("/images/product.jpg")
                .createdAt(now)
                .build();
        
        assertEquals("2", media.getId());
        assertEquals("product456", media.getOwnerId());
        assertEquals(MediaOwnerType.PRODUCT, media.getOwnerType());
        assertEquals("/images/product.jpg", media.getImagePath());
        assertEquals(now, media.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Media media = new Media();
        Instant now = Instant.now();
        
        media.setId("3");
        media.setOwnerId("owner789");
        media.setOwnerType(MediaOwnerType.USER);
        media.setImagePath("/images/test.png");
        media.setCreatedAt(now);
        
        assertEquals("3", media.getId());
        assertEquals("owner789", media.getOwnerId());
        assertEquals(MediaOwnerType.USER, media.getOwnerType());
        assertEquals("/images/test.png", media.getImagePath());
        assertEquals(now, media.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        Instant now = Instant.now();
        Media media1 = new Media("1", "owner1", MediaOwnerType.USER, "/path1.png", now);
        Media media2 = new Media("1", "owner1", MediaOwnerType.USER, "/path1.png", now);
        Media media3 = new Media("2", "owner2", MediaOwnerType.PRODUCT, "/path2.png", now);
        
        assertEquals(media1, media2);
        assertEquals(media1.hashCode(), media2.hashCode());
        assertNotEquals(media1, media3);
    }

    @Test
    void testToString() {
        Media media = Media.builder()
                .id("1")
                .ownerId("owner123")
                .ownerType(MediaOwnerType.USER)
                .imagePath("/test.png")
                .build();
        String str = media.toString();
        assertNotNull(str);
        assertTrue(str.contains("1"));
        assertTrue(str.contains("owner123"));
    }

    @Test
    void testUserOwnerType() {
        Media media = Media.builder()
                .ownerType(MediaOwnerType.USER)
                .build();
        assertEquals(MediaOwnerType.USER, media.getOwnerType());
    }

    @Test
    void testProductOwnerType() {
        Media media = Media.builder()
                .ownerType(MediaOwnerType.PRODUCT)
                .build();
        assertEquals(MediaOwnerType.PRODUCT, media.getOwnerType());
    }

    @Test
    void testNullCreatedAt() {
        Media media = Media.builder()
                .id("1")
                .ownerId("owner")
                .ownerType(MediaOwnerType.USER)
                .imagePath("/path.png")
                .build();
        assertNull(media.getCreatedAt());
    }

    @Test
    void testEqualsWithNull() {
        Media media = new Media();
        assertNotEquals(null, media);
    }

    @Test
    void testEqualsWithDifferentClass() {
        Media media = new Media();
        assertNotEquals("string", media);
    }
}
