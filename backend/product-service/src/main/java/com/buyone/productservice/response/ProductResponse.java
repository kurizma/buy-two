package com.buyone.productservice.response;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String userId;
    private String categoryId;
    private java.util.List<String> images;
    
    // private String mediaId;
    // Optional:
    // private Instant createdAt;
}
