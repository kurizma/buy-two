package com.buyone.mediaservice.listener;

import com.buyone.mediaservice.model.Media;
import com.buyone.mediaservice.model.MediaOwnerType;
import com.buyone.mediaservice.repository.MediaRepository;
import com.buyone.mediaservice.service.StorageService;
import com.buyone.productservice.event.ProductDeletedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductEventListenerTests {
    
    @Mock
    private MediaRepository mediaRepository;
    
    @Mock
    private StorageService storageService;
    
    @InjectMocks
    private ProductEventListener listener;
    
    @Test
    void onProductDeleted_deletesAllMediaForProduct() {
        String productId = "prod-1";
        ProductDeletedEvent event = ProductDeletedEvent.builder()
                .productId(productId)
                .sellerId("seller-1")
                .build();
        
        Media m1 = Media.builder().id("m1").imagePath("images/1.jpg").ownerId(productId).ownerType(MediaOwnerType.PRODUCT).build();
        Media m2 = Media.builder().id("m2").imagePath("images/2.jpg").ownerId(productId).ownerType(MediaOwnerType.PRODUCT).build();
        
        when(mediaRepository.findAllByOwnerIdAndOwnerType(productId, MediaOwnerType.PRODUCT))
                .thenReturn(List.of(m1, m2));
        
        listener.onProductDeleted(event);
        
        verify(storageService).delete("images/1.jpg");
        verify(storageService).delete("images/2.jpg");
        verify(mediaRepository).deleteById("m1");
        verify(mediaRepository).deleteById("m2");
    }
    
    @Test
    void onProductDeleted_handlesNoMedia() {
        ProductDeletedEvent event = ProductDeletedEvent.builder()
                .productId("prod-empty")
                .sellerId("seller-1")
                .build();
        
        when(mediaRepository.findAllByOwnerIdAndOwnerType("prod-empty", MediaOwnerType.PRODUCT))
                .thenReturn(List.of());
        
        listener.onProductDeleted(event);
        
        verify(storageService, never()).delete(any());
        verify(mediaRepository, never()).deleteById(any());
    }
    
    @Test
    void onProductDeleted_continuesOnStorageError() {
        String productId = "prod-1";
        ProductDeletedEvent event = ProductDeletedEvent.builder()
                .productId(productId)
                .sellerId("seller-1")
                .build();
        
        Media m1 = Media.builder().id("m1").imagePath("images/1.jpg").ownerId(productId).ownerType(MediaOwnerType.PRODUCT).build();
        Media m2 = Media.builder().id("m2").imagePath("images/2.jpg").ownerId(productId).ownerType(MediaOwnerType.PRODUCT).build();
        
        when(mediaRepository.findAllByOwnerIdAndOwnerType(productId, MediaOwnerType.PRODUCT))
                .thenReturn(List.of(m1, m2));
        doThrow(new RuntimeException("Storage error")).when(storageService).delete("images/1.jpg");
        
        listener.onProductDeleted(event);
        
        // m2 should still be cleaned up despite m1 failure
        verify(storageService).delete("images/2.jpg");
        verify(mediaRepository).deleteById("m2");
    }
}
