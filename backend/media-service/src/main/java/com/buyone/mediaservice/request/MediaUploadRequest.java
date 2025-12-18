package com.buyone.mediaservice.request;

import com.buyone.mediaservice.model.MediaOwnerType;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MediaUploadRequest {
    
    @NotNull(message = "File is required")
    private MultipartFile file;
    
    @NotBlank(message = "Owner ID is required")
    private String ownerId;
    
    @NotNull(message = "Owner type is required")
    private MediaOwnerType ownerType;
}