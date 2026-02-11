package com.buyone.mediaservice.response;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class MediaResponseTests {

    @Test
    void testRecordConstruction() {
        Instant now = Instant.now();
        MediaResponse response = new MediaResponse("1", "owner123", "/media/images/1", now);
        
        assertEquals("1", response.id());
        assertEquals("owner123", response.ownerId());
        assertEquals("/media/images/1", response.url());
        assertEquals(now, response.createdAt());
    }

    @Test
    void testEqualsAndHashCode() {
        Instant now = Instant.now();
        MediaResponse response1 = new MediaResponse("1", "owner", "/url", now);
        MediaResponse response2 = new MediaResponse("1", "owner", "/url", now);
        MediaResponse response3 = new MediaResponse("2", "other", "/other", now);
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        MediaResponse response = new MediaResponse("1", "owner", "/media/images/1", Instant.now());
        String str = response.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("1"));
        assertTrue(str.contains("owner"));
    }

    @Test
    void testNullCreatedAt() {
        MediaResponse response = new MediaResponse("1", "owner", "/url", null);
        assertNull(response.createdAt());
    }

    @Test
    void testNullValues() {
        MediaResponse response = new MediaResponse(null, null, null, null);
        assertNull(response.id());
        assertNull(response.ownerId());
        assertNull(response.url());
        assertNull(response.createdAt());
    }

    @Test
    void testDifferentIds() {
        Instant now = Instant.now();
        MediaResponse response1 = new MediaResponse("1", "owner", "/url", now);
        MediaResponse response2 = new MediaResponse("2", "owner", "/url", now);
        assertNotEquals(response1, response2);
    }

    @Test
    void testDifferentOwners() {
        Instant now = Instant.now();
        MediaResponse response1 = new MediaResponse("1", "owner1", "/url", now);
        MediaResponse response2 = new MediaResponse("1", "owner2", "/url", now);
        assertNotEquals(response1, response2);
    }
}
