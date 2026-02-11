package com.buyone.mediaservice.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeleteMediaResponseTests {

    @Test
    void testRecordConstruction() {
        DeleteMediaResponse response = new DeleteMediaResponse("media123", "Media deleted successfully");
        
        assertEquals("media123", response.mediaId());
        assertEquals("Media deleted successfully", response.message());
    }

    @Test
    void testEqualsAndHashCode() {
        DeleteMediaResponse response1 = new DeleteMediaResponse("1", "Deleted");
        DeleteMediaResponse response2 = new DeleteMediaResponse("1", "Deleted");
        DeleteMediaResponse response3 = new DeleteMediaResponse("2", "Deleted");
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        DeleteMediaResponse response = new DeleteMediaResponse("mediaId", "Success message");
        String str = response.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("mediaId"));
        assertTrue(str.contains("Success message"));
    }

    @Test
    void testNullMediaId() {
        DeleteMediaResponse response = new DeleteMediaResponse(null, "Message");
        assertNull(response.mediaId());
    }

    @Test
    void testNullMessage() {
        DeleteMediaResponse response = new DeleteMediaResponse("id", null);
        assertNull(response.message());
    }

    @Test
    void testAllNullValues() {
        DeleteMediaResponse response = new DeleteMediaResponse(null, null);
        assertNull(response.mediaId());
        assertNull(response.message());
    }

    @Test
    void testDifferentMessages() {
        DeleteMediaResponse response1 = new DeleteMediaResponse("1", "Message A");
        DeleteMediaResponse response2 = new DeleteMediaResponse("1", "Message B");
        assertNotEquals(response1, response2);
    }

    @Test
    void testDifferentIds() {
        DeleteMediaResponse response1 = new DeleteMediaResponse("id1", "Same");
        DeleteMediaResponse response2 = new DeleteMediaResponse("id2", "Same");
        assertNotEquals(response1, response2);
    }
}
