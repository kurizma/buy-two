package com.buyone.mediaservice.response;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MediaListResponseTests {

    @Test
    void testRecordConstruction() {
        List<MediaResponse> images = List.of(
            new MediaResponse("1", "owner", "/url1", Instant.now()),
            new MediaResponse("2", "owner", "/url2", Instant.now())
        );
        MediaListResponse response = new MediaListResponse(images, 2, 10);
        
        assertEquals(2, response.images().size());
        assertEquals(2, response.count());
        assertEquals(10, response.maxImages());
    }

    @Test
    void testEmptyList() {
        MediaListResponse response = new MediaListResponse(List.of(), 0, 10);
        
        assertTrue(response.images().isEmpty());
        assertEquals(0, response.count());
        assertEquals(10, response.maxImages());
    }

    @Test
    void testEqualsAndHashCode() {
        List<MediaResponse> images = List.of(new MediaResponse("1", "owner", "/url", null));
        MediaListResponse response1 = new MediaListResponse(images, 1, 5);
        MediaListResponse response2 = new MediaListResponse(images, 1, 5);
        MediaListResponse response3 = new MediaListResponse(images, 2, 10);
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        MediaListResponse response = new MediaListResponse(List.of(), 0, 5);
        String str = response.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("0") || str.contains("5"));
    }

    @Test
    void testNullImages() {
        MediaListResponse response = new MediaListResponse(null, 0, 5);
        assertNull(response.images());
    }

    @Test
    void testMaxImagesLimit() {
        List<MediaResponse> images = List.of();
        MediaListResponse response = new MediaListResponse(images, 0, 20);
        assertEquals(20, response.maxImages());
    }

    @Test
    void testWithMultipleImages() {
        Instant now = Instant.now();
        List<MediaResponse> images = List.of(
            new MediaResponse("1", "owner1", "/image1.png", now),
            new MediaResponse("2", "owner1", "/image2.png", now),
            new MediaResponse("3", "owner1", "/image3.png", now)
        );
        MediaListResponse response = new MediaListResponse(images, 3, 10);
        
        assertEquals(3, response.images().size());
        assertEquals("1", response.images().get(0).id());
        assertEquals("2", response.images().get(1).id());
        assertEquals("3", response.images().get(2).id());
    }
}
