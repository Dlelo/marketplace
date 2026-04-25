package com.example.marketplace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.utils.StringUtils;

import java.net.URI;

@Slf4j
@Configuration
public class SpacesConfig {

    @Value("${do.spaces.access-key:}")
    private String accessKey;

    @Value("${do.spaces.secret-key:}")
    private String secretKey;

    @Value("${do.spaces.region:fra1}")
    private String region;

    @Value("${do.spaces.endpoint:https://fra1.digitaloceanspaces.com}")
    private String endpoint;

    /**
     * Builds the S3 client used for DigitalOcean Spaces uploads. When credentials
     * are missing (typical in local dev where no uploads happen) we still return
     * a client built with placeholder credentials so the Spring context can come
     * up — any real upload call will then fail at request time with a clear error,
     * rather than blocking app startup.
     */
    @Bean
    public S3Client s3Client() {
        boolean credsMissing = StringUtils.isBlank(accessKey) || StringUtils.isBlank(secretKey);
        if (credsMissing) {
            log.warn(
                "DigitalOcean Spaces credentials are blank — uploads will fail at runtime. "
                + "Set SPACES_ACCESS_KEY / SPACES_SECRET_KEY to enable file uploads."
            );
        }

        String key = credsMissing ? "local-dev-placeholder" : accessKey;
        String secret = credsMissing ? "local-dev-placeholder" : secretKey;

        return S3Client.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(key, secret))
                )
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
    }
}
