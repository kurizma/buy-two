package com.buyone.mediaservice.service.impl;

import com.buyone.mediaservice.config.CloudflareR2Properties;
import com.buyone.mediaservice.exception.InvalidFileException;
import com.buyone.mediaservice.exception.MediaNotFoundException;
import com.buyone.mediaservice.exception.BadRequestException;
import com.buyone.mediaservice.service.StorageService;
import com.buyone.mediaservice.config.CloudflareR2Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    
    private final S3Client r2S3Client;
    private final CloudflareR2Properties props;
    
    @Override
    public String store(MultipartFile file, String mediaId) {
        if (file.isEmpty()) {
            throw new BadRequestException("Cannot store empty file");
        }
        
        long maxBytes = 2L * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new InvalidFileException("File too large, max 2MB");
        }
        
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new InvalidFileException("Only image/* uploads are allowed");
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String key = "media/" + mediaId + extension;
        
        try {
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            
            r2S3Client.putObject(putReq, RequestBody.fromBytes(file.getBytes()));
            return key;
        } catch (IOException e) {
            throw new InvalidFileException("Failed to read uploaded file", e);
        } catch (Exception e) {
            // Any AWS/R2 error while putting object → 500 via global handler
            throw new RuntimeException("Failed to store file in R2", e);
        }
    }
    
    @Override
    public Resource loadAsResource(String imagePath) {
        try {
            GetObjectRequest getReq = GetObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(imagePath)
                    .build();
            
            ResponseInputStream<GetObjectResponse> s3Object =
                    r2S3Client.getObject(getReq);
            
            byte[] bytes = s3Object.readAllBytes();
            return new InputStreamResource(new ByteArrayInputStream(bytes));
        } catch (NoSuchKeyException e) {
            throw new MediaNotFoundException(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read object from R2", e);
        }
    }
    
    @Override
    public void delete(String imagePath) {
        try {
            DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(imagePath)
                    .build();
            r2S3Client.deleteObject(delReq);
        } catch (NoSuchKeyException e) {
            // Up to you: either ignore or signal 404
            throw new MediaNotFoundException(imagePath);
        }
    }
}
