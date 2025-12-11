package com.example.marketplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class SpacesConfig {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.ME_CENTRAL_1)   // Spaces uses any AWS region, pick one
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        System.getenv("SPACES_KEY"),
                                        System.getenv("SPACES_SECRET")
                                )
                        )
                )
                .endpointOverride(URI.create("https://fra1.digitaloceanspaces.com")) // change to your region
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .httpClient(UrlConnectionHttpClient.builder().build())
                .build();
    }
}
