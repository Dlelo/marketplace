package com.example.marketplace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;

    @Value("${spaces.bucket}")
    private String bucket;

    @Value("${spaces.endpoint}")   // e.g. https://yaya-docs.fra1.digitaloceanspaces.com
    private String endpoint;

    public String upload(MultipartFile file) {
        try {
            String key = "documents/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .acl("public-read")
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );

            return endpoint + "/" + key;

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
