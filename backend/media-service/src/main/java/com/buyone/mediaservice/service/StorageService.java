package com.buyone.mediaservice.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    
    String store(MultipartFile file, String mediaId);
    
    Resource loadAsResource(String imagePath);
    
    void delete(String imagePath);
}
