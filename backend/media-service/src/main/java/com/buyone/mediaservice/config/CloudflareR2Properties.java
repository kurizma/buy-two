package com.buyone.mediaservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

//the what

@Data
@ConfigurationProperties(prefix = "cloudflare.r2")
public class CloudflareR2Properties {
    
    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private String endpoint;
    private String bucket;
}
