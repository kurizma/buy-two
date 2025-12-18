package com.buyone.mediaservice.config;

import java.net.URI;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import com.buyone.mediaservice.config.CloudflareR2Properties;


// the HOW

@Configuration
@EnableConfigurationProperties(CloudflareR2Properties.class)
@RequiredArgsConstructor
public class CloudflareR2Config {
    
    private final CloudflareR2Properties props;
    
    @Bean
    public S3Client r2S3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                props.getAccessKeyId(),
                props.getSecretAccessKey()
        );
        
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(props.getRegion())) // "auto" for R2 [web:12][web:32]
                .endpointOverride(URI.create(props.getEndpoint())) // https://<account>.r2.cloudflarestorage.com [web:6][web:32]
                .build();
    }
}
