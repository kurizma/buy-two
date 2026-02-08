package com.buyone.mediaservice.controller;

import com.buyone.mediaservice.exception.GlobalExceptionHandler;
import com.buyone.mediaservice.exception.ForbiddenException;
import com.buyone.mediaservice.exception.InvalidFileException;
import com.buyone.mediaservice.exception.MediaNotFoundException;
import com.buyone.mediaservice.model.Media;
import com.buyone.mediaservice.model.MediaOwnerType;
import com.buyone.mediaservice.response.DeleteMediaResponse;
import com.buyone.mediaservice.response.MediaResponse;
import com.buyone.mediaservice.service.MediaService;
import com.buyone.mediaservice.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MediaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class MediaControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private MediaService mediaService;
    
    @MockBean
    private StorageService storageService;
    
    // ========== GET /media/images/product/{productId} ==========
    
    @Test
    void listMediaForProduct_returns200_withMediaList() throws Exception {
        MediaResponse mr = new MediaResponse("m1", "prod-1", "http://img.com/1.jpg", Instant.now());
        when(mediaService.mediaListForProduct("prod-1")).thenReturn(List.of(mr));
        
        mockMvc.perform(get("/media/images/product/prod-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.count").value(1))
                .andExpect(jsonPath("$.data.maxImages").value(5))
                .andExpect(jsonPath("$.data.images", hasSize(1)))
                .andExpect(jsonPath("$.data.images[0].id").value("m1"));
    }
    
    @Test
    void listMediaForProduct_returns200_withEmptyList() throws Exception {
        when(mediaService.mediaListForProduct("prod-x")).thenReturn(List.of());
        
        mockMvc.perform(get("/media/images/product/prod-x"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(0))
                .andExpect(jsonPath("$.data.images", hasSize(0)));
    }
    
    // ========== GET /media/images/{mediaId} ==========
    
    @Test
    void getMedia_returns200_withMediaResponse() throws Exception {
        MediaResponse mr = new MediaResponse("m1", "owner-1", "http://img.com/1.jpg", Instant.now());
        when(mediaService.getMedia("m1")).thenReturn(mr);
        
        mockMvc.perform(get("/media/images/m1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("m1"));
    }
    
    @Test
    void getMedia_returns404_whenNotFound() throws Exception {
        when(mediaService.getMedia("bad-id")).thenThrow(new MediaNotFoundException("Media not found"));
        
        mockMvc.perform(get("/media/images/bad-id"))
                .andExpect(status().isNotFound());
    }
    
    // ========== GET /media/images/{mediaId}/file ==========
    
    @Test
    void getImageFile_returns200_withImageResource() throws Exception {
        Media media = Media.builder().id("m1").imagePath("images/test.jpg").build();
        Resource resource = new ByteArrayResource(new byte[]{1, 2, 3});
        when(mediaService.findMediaEntity("m1")).thenReturn(media);
        when(storageService.loadAsResource("images/test.jpg")).thenReturn(resource);
        
        mockMvc.perform(get("/media/images/m1/file"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }
    
    // ========== POST /media/images ==========
    
    @Test
    void uploadImage_returns201_whenValid() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3});
        
        MediaResponse mr = new MediaResponse("m1", "prod-1", "http://img.com/1.jpg", Instant.now());
        when(mediaService.uploadImage(any(), eq("prod-1"), eq(MediaOwnerType.PRODUCT), eq("user-1"), eq("SELLER")))
                .thenReturn(mr);
        
        mockMvc.perform(multipart("/media/images")
                        .file(file)
                        .param("ownerId", "prod-1")
                        .param("ownerType", "PRODUCT")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Image uploaded successfully"))
                .andExpect(jsonPath("$.data.id").value("m1"));
    }
    
    @Test
    void uploadImage_returns400_whenInvalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "doc.pdf",
                "application/pdf", new byte[]{1, 2, 3});
        
        when(mediaService.uploadImage(any(), any(), any(), any(), any()))
                .thenThrow(new InvalidFileException("Only image files are allowed"));
        
        mockMvc.perform(multipart("/media/images")
                        .file(file)
                        .param("ownerId", "prod-1")
                        .param("ownerType", "PRODUCT")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void uploadImage_returns403_whenNonOwner() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3});
        
        when(mediaService.uploadImage(any(), any(), any(), any(), any()))
                .thenThrow(new ForbiddenException("Not authorized"));
        
        mockMvc.perform(multipart("/media/images")
                        .file(file)
                        .param("ownerId", "prod-1")
                        .param("ownerType", "PRODUCT")
                        .header("X-USER-ID", "attacker-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isForbidden());
    }
    
    // ========== PUT /media/images/{mediaId} ==========
    
    @Test
    void updateMedia_returns200_whenValid() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "updated.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[]{4, 5, 6});
        
        MediaResponse mr = new MediaResponse("m1", "prod-1", "http://img.com/updated.jpg", Instant.now());
        when(mediaService.updateMedia(any(), eq("m1"), eq("user-1"), eq("SELLER"))).thenReturn(mr);
        
        mockMvc.perform(multipart("/media/images/m1")
                        .file(file)
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "SELLER")
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Image updated successfully"))
                .andExpect(jsonPath("$.data.id").value("m1"));
    }
    
    @Test
    void updateMedia_returns404_whenNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "new.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[]{1});
        
        when(mediaService.updateMedia(any(), eq("bad-id"), any(), any()))
                .thenThrow(new MediaNotFoundException("Media not found"));
        
        mockMvc.perform(multipart("/media/images/bad-id")
                        .file(file)
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "SELLER")
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isNotFound());
    }
    
    // ========== DELETE /media/images/{mediaId} ==========
    
    @Test
    void deleteMedia_returns200_whenAuthorized() throws Exception {
        DeleteMediaResponse dr = new DeleteMediaResponse("m1", "Deleted");
        when(mediaService.deleteMedia("m1", "user-1", "SELLER")).thenReturn(dr);
        
        mockMvc.perform(delete("/media/images/m1")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deleted successfully"))
                .andExpect(jsonPath("$.data.mediaId").value("m1"));
    }
    
    @Test
    void deleteMedia_returns403_whenForbidden() throws Exception {
        when(mediaService.deleteMedia("m1", "attacker", "CLIENT"))
                .thenThrow(new ForbiddenException("Not authorized"));
        
        mockMvc.perform(delete("/media/images/m1")
                        .header("X-USER-ID", "attacker")
                        .header("X-USER-ROLE", "CLIENT"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void deleteMedia_returns404_whenMediaNotFound() throws Exception {
        when(mediaService.deleteMedia("bad-id", "user-1", "SELLER"))
                .thenThrow(new MediaNotFoundException("Media not found"));
        
        mockMvc.perform(delete("/media/images/bad-id")
                        .header("X-USER-ID", "user-1")
                        .header("X-USER-ROLE", "SELLER"))
                .andExpect(status().isNotFound());
    }
}
