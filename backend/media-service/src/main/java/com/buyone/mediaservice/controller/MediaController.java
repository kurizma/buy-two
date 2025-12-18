package com.buyone.mediaservice.controller;

import com.buyone.mediaservice.model.Media;
import com.buyone.mediaservice.request.MediaUploadRequest;
import com.buyone.mediaservice.response.MediaResponse;
import com.buyone.mediaservice.response.MediaListResponse;
import com.buyone.mediaservice.response.DeleteMediaResponse;
import com.buyone.mediaservice.response.ApiResponse;
import com.buyone.mediaservice.service.MediaService;
import com.buyone.mediaservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.util.List;

@RestController
@RequestMapping("/media/images")
@RequiredArgsConstructor
public class MediaController {
    
    private final MediaService mediaService;
    private final StorageService storageService;
    // use @Value or service constant and put into yml
    private static final int MAX_IMAGES_PER_PRODUCT = 5;
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<MediaListResponse>> listMediaForProduct(@PathVariable String productId) {
        List<MediaResponse> responses = mediaService.mediaListForProduct(productId);
        MediaListResponse payload = new MediaListResponse(responses, responses.size(), MAX_IMAGES_PER_PRODUCT);
        ApiResponse<MediaListResponse> response = ApiResponse.<MediaListResponse>builder()
                .success(true)
                .data(payload)
                .build();
        return ResponseEntity.ok(response);
    }
    
    //    Right now, your getMedia is perfect for APIs (Angular) to fetch metadata,
    //    but to actually display the avatar image in <img>,
    //    you’ll eventually need an endpoint that returns image/jpeg bytes.
    //    For now, conceptually:
    //    Yes: GET /media/images/{mediaId} is the avatar metadata call.
    
    // get metadata for specific media file, ex. Avatar
    @GetMapping("/{mediaId}")
    public ResponseEntity<ApiResponse<MediaResponse>> getMedia(@PathVariable String mediaId) {
        MediaResponse media = mediaService.getMedia(mediaId);
        ApiResponse<MediaResponse> response = ApiResponse.<MediaResponse>builder()
                .success(true)
                .data(media)
                .build();
        return ResponseEntity.ok(response);
    }
    
    // raw image api
    @GetMapping("/{mediaId}/file")
    public ResponseEntity<Resource> getImageFile(@PathVariable String mediaId) {
        Media media = mediaService.findMediaEntity(mediaId); // or reuse getMedia + repo
        Resource resource = storageService.loadAsResource(media.getImagePath());
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // or detect from file/metadata
                .body(resource);
    }
    
    
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MediaResponse>> uploadImage(
            @Valid @ModelAttribute MediaUploadRequest request,
            @RequestHeader("X-USER-ID") String currentUserId,
            @RequestHeader("X-USER-ROLE") String currentUserRole
    ) {
        MediaResponse media = mediaService.uploadImage(
                request.getFile(),
                request.getOwnerId(),
                request.getOwnerType(),
                currentUserId,
                currentUserRole
        );
        ApiResponse<MediaResponse> response = ApiResponse.<MediaResponse>builder()
                .success(true)
                .message("Image uploaded successfully")
                .data(media)
                .build();
        return ResponseEntity.status(201).body(response);
    }
    
    @PutMapping(value = "/{mediaId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MediaResponse>> updateMedia(
            @PathVariable String mediaId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-USER-ID") String currentUserId,
            @RequestHeader("X-USER-ROLE") String currentUserRole
    ) {
        MediaResponse media = mediaService.updateMedia(file, mediaId, currentUserId, currentUserRole);
        ApiResponse<MediaResponse> response = ApiResponse.<MediaResponse>builder()
                .success(true)
                .message("Image updated successfully")
                .data(media)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<ApiResponse<DeleteMediaResponse>>  deleteMedia(@PathVariable String mediaId,
            @RequestHeader("X-USER-ID") String currentUserId,
            @RequestHeader("X-USER-ROLE") String currentUserRole
    ) {
        DeleteMediaResponse deleted = mediaService.deleteMedia(mediaId, currentUserId, currentUserRole);
        ApiResponse<DeleteMediaResponse> response = ApiResponse.<DeleteMediaResponse>builder()
                .success(true)
                .message("Deleted successfully")
                .data(deleted)
                .build();
        return ResponseEntity.ok(response);
    }
}
