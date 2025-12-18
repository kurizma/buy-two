package com.buyone.mediaservice.listener;

import com.buyone.mediaservice.model.MediaOwnerType;
import com.buyone.mediaservice.repository.MediaRepository;
import com.buyone.mediaservice.service.StorageService;
import com.buyone.productservice.event.ProductDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {
    
    private final MediaRepository mediaRepository;
    private final StorageService storageService;
    
    @KafkaListener(
            topics = "${app.kafka.topic.product-deleted}",
            groupId = "media-service"
    )
    public void onProductDeleted(ProductDeletedEvent event) {
        log.info("🗑️ Cleaning up images for product: {}", event.getProductId());
        // Find all media for this product
        var medias = mediaRepository.findAllByOwnerIdAndOwnerType(
                event.getProductId(),
                MediaOwnerType.PRODUCT
        );
        
        for (var media : medias) {
            try {
                storageService.delete(media.getImagePath());
                mediaRepository.deleteById(media.getId());
                log.info("✅ Deleted image: {}", media.getId());  // ADD LOG
            } catch (Exception e) {
                log.error("❌ Failed to delete image {}: {}", media.getId(), e.getMessage());
            }
        }
        log.info("✅ Product cleanup complete: {}", event.getProductId());  // ADD LOG
    }
        // Optional: log or produce audit event
}
