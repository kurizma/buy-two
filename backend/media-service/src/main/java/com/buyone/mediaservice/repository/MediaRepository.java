package com.buyone.mediaservice.repository;

import com.buyone.mediaservice.model.Media;
import com.buyone.mediaservice.model.MediaOwnerType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MediaRepository extends MongoRepository<Media, String> {
    long countByOwnerIdAndOwnerType(String ownerId, MediaOwnerType ownerType);
    List<Media> findAllByOwnerIdAndOwnerType(String ownerId, MediaOwnerType ownerType);
}
