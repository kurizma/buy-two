package com.buyone.mediaservice.request;

import com.buyone.mediaservice.model.MediaOwnerType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import static org.junit.jupiter.api.Assertions.*;

class MediaUploadRequestTests {

    @Test
    void testDefaultConstructor() {
        MediaUploadRequest request = new MediaUploadRequest();
        assertNotNull(request);
        assertNull(request.getFile());
        assertNull(request.getOwnerId());
        assertNull(request.getOwnerType());
    }

    @Test
    void testSettersAndGetters() {
        MediaUploadRequest request = new MediaUploadRequest();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());
        
        request.setFile(file);
        request.setOwnerId("user123");
        request.setOwnerType(MediaOwnerType.USER);
        
        assertEquals(file, request.getFile());
        assertEquals("user123", request.getOwnerId());
        assertEquals(MediaOwnerType.USER, request.getOwnerType());
    }

    @Test
    void testUserOwnerType() {
        MediaUploadRequest request = new MediaUploadRequest();
        request.setOwnerType(MediaOwnerType.USER);
        assertEquals(MediaOwnerType.USER, request.getOwnerType());
    }

    @Test
    void testProductOwnerType() {
        MediaUploadRequest request = new MediaUploadRequest();
        request.setOwnerType(MediaOwnerType.PRODUCT);
        assertEquals(MediaOwnerType.PRODUCT, request.getOwnerType());
    }

    @Test
    void testEqualsAndHashCode() {
        MediaUploadRequest request1 = new MediaUploadRequest();
        request1.setOwnerId("owner1");
        request1.setOwnerType(MediaOwnerType.USER);
        
        MediaUploadRequest request2 = new MediaUploadRequest();
        request2.setOwnerId("owner1");
        request2.setOwnerType(MediaOwnerType.USER);
        
        MediaUploadRequest request3 = new MediaUploadRequest();
        request3.setOwnerId("owner2");
        request3.setOwnerType(MediaOwnerType.PRODUCT);
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void testToString() {
        MediaUploadRequest request = new MediaUploadRequest();
        request.setOwnerId("testOwner");
        request.setOwnerType(MediaOwnerType.USER);
        
        String str = request.toString();
        assertNotNull(str);
        assertTrue(str.contains("testOwner"));
    }

    @Test
    void testWithMockFile() {
        MediaUploadRequest request = new MediaUploadRequest();
        MockMultipartFile pngFile = new MockMultipartFile("image", "avatar.png", "image/png", new byte[100]);
        
        request.setFile(pngFile);
        
        assertNotNull(request.getFile());
        assertEquals("avatar.png", request.getFile().getOriginalFilename());
        assertEquals("image/png", request.getFile().getContentType());
    }

    @Test
    void testNullValues() {
        MediaUploadRequest request = new MediaUploadRequest();
        request.setFile(null);
        request.setOwnerId(null);
        request.setOwnerType(null);
        
        assertNull(request.getFile());
        assertNull(request.getOwnerId());
        assertNull(request.getOwnerType());
    }
}
