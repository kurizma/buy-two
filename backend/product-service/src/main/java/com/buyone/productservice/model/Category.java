package com.buyone.productservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document("categories")
public class Category {
    @Id
    private String id;
    private String slug;
    private String name;
    private String icon;
    private String description;
}
