package com.buyone.mediaservice.service;

import com.buyone.mediaservice.model.Media;
import com.buyone.mediaservice.model.MediaOwnerType;
import com.buyone.mediaservice.response.MediaResponse;
import com.buyone.mediaservice.response.MediaListResponse;
import com.buyone.mediaservice.response.DeleteMediaResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface MediaService {
    MediaResponse uploadImage(MultipartFile file,
                              String ownerId,
                              MediaOwnerType ownerType,
                              String currentUserId,
                              String currentUserRole);
    
    MediaResponse getMedia(String id);
    
    MediaResponse updateMedia(MultipartFile file,
                              String mediaId,
                              String currentUserId,
                              String currentUserRole);
    
    DeleteMediaResponse deleteMedia(String id,
                                    String currentUserId,
                                    String currentUserRole);
    
    List<MediaResponse> mediaListForProduct(String productId);
    
    // calling from storage to media
    Media findMediaEntity(String id);
}
