package com.buyone.mediaservice.service;

import com.buyone.mediaservice.config.CloudflareR2Properties;
import com.buyone.mediaservice.exception.BadRequestException;
import com.buyone.mediaservice.exception.InvalidFileException;
import com.buyone.mediaservice.exception.MediaNotFoundException;
import com.buyone.mediaservice.service.impl.StorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceImplTests {
    
    @Mock
    private S3Client r2S3Client;
    
    @Mock
    private CloudflareR2Properties props;
    
    @InjectMocks
    private StorageServiceImpl storageService;
    
    @BeforeEach
    void setUp() {
        lenient().when(props.getBucket()).thenReturn("test-bucket");
    }
    
    // ========== store ==========
    
    @Test
    void store_returnsKey_whenValidImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", new byte[1024]);
        
        when(r2S3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        
        String key = storageService.store(file, "media-123");
        
        assertThat(key).isEqualTo("media/media-123.jpg");
        verify(r2S3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
    
    @Test
    void store_returnsKeyWithoutExtension_whenNoFilename() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", null, "image/png", new byte[100]);
        
        when(r2S3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        
        String key = storageService.store(file, "media-456");
        
        assertThat(key).isEqualTo("media/media-456");
    }
    
    @Test
    void store_throwsBadRequest_whenFileEmpty() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]);
        
        assertThatThrownBy(() -> storageService.store(file, "media-1"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("empty file");
    }
    
    @Test
    void store_throwsInvalidFile_whenFileTooLarge() {
        byte[] largeContent = new byte[3 * 1024 * 1024]; // 3MB > 2MB limit
        MockMultipartFile file = new MockMultipartFile(
                "file", "big.jpg", "image/jpeg", largeContent);
        
        assertThatThrownBy(() -> storageService.store(file, "media-1"))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("max 2MB");
    }
    
    @Test
    void store_throwsInvalidFile_whenNotImage() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", new byte[100]);
        
        assertThatThrownBy(() -> storageService.store(file, "media-1"))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("image/*");
    }
    
    @Test
    void store_throwsInvalidFile_whenContentTypeNull() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "noext", null, new byte[100]);
        
        assertThatThrownBy(() -> storageService.store(file, "media-1"))
                .isInstanceOf(InvalidFileException.class);
    }
    
    @Test
    void store_throwsRuntime_whenS3Fails() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "img.jpg", "image/jpeg", new byte[100]);
        
        when(r2S3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("S3 error").build());
        
        assertThatThrownBy(() -> storageService.store(file, "media-1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to store file");
    }
    
    // ========== loadAsResource ==========
    
    @Test
    @SuppressWarnings("unchecked")
    void loadAsResource_returnsResource_whenExists() throws IOException {
        byte[] content = "image-data".getBytes();
        ResponseInputStream<GetObjectResponse> responseStream =
                new ResponseInputStream<>(
                        GetObjectResponse.builder().build(),
                        AbortableInputStream.create(new ByteArrayInputStream(content)));
        
        when(r2S3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);
        
        Resource resource = storageService.loadAsResource("media/img.jpg");
        
        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
    }
    
    @Test
    void loadAsResource_throwsMediaNotFound_whenNoSuchKey() {
        when(r2S3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("not found").build());
        
        assertThatThrownBy(() -> storageService.loadAsResource("media/missing.jpg"))
                .isInstanceOf(MediaNotFoundException.class);
    }
    
    // ========== delete ==========
    
    @Test
    void delete_succeeds_whenKeyExists() {
        when(r2S3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());
        
        storageService.delete("media/img.jpg");
        
        verify(r2S3Client).deleteObject(any(DeleteObjectRequest.class));
    }
    
    @Test
    void delete_throwsMediaNotFound_whenNoSuchKey() {
        when(r2S3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("not found").build());
        
        assertThatThrownBy(() -> storageService.delete("media/missing.jpg"))
                .isInstanceOf(MediaNotFoundException.class);
    }
}
