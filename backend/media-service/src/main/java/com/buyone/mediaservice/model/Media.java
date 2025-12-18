package com.buyone.mediaservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("media")
public class Media {
    
    @Id
    private String id;
    private String ownerId; // userId or productId
    private MediaOwnerType ownerType; // USER or PRODUCT
    private String imagePath;   // where the file is stored (relative path or URL)
    
    @CreatedDate
    private Instant createdAt;
}
