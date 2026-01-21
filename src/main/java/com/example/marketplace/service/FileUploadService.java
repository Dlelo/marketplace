package com.example.marketplace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;

    @Value("${do.spaces.bucket}")
    private String bucket;

    @Value("${do.spaces.endpoint}")   // e.g. https://your-bucket.region.digitaloceanspaces.com
    private String endpoint;

    // Allowed file types
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/jpg");
    private static final List<String> ALLOWED_PDF_TYPES = Arrays.asList("application/pdf");

    // File size limits (in bytes)
    private static final long MAX_PROFILE_PICTURE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_DOCUMENT_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Upload profile picture (JPEG only, max 5MB)
     */
    public String uploadProfilePicture(MultipartFile file) {
        validateFile(file, ALLOWED_IMAGE_TYPES, MAX_PROFILE_PICTURE_SIZE, "Profile picture");

        try {
            String key = "profile-pictures/" + UUID.randomUUID() + "-" + sanitizeFilename(file.getOriginalFilename());

            return uploadToS3(file, key);

        } catch (IOException e) {
            throw new RuntimeException("Profile picture upload failed", e);
        }
    }

    /**
     * Upload national ID document (PDF only, max 10MB)
     */
    public String uploadNationalId(MultipartFile file) {
        validateFile(file, ALLOWED_PDF_TYPES, MAX_DOCUMENT_SIZE, "National ID");

        try {
            String key = "national-ids/" + UUID.randomUUID() + "-" + sanitizeFilename(file.getOriginalFilename());

            return uploadToS3(file, key);

        } catch (IOException e) {
            throw new RuntimeException("National ID upload failed", e);
        }
    }

    /**
     * Upload good conduct certificate (PDF only, max 10MB)
     */
    public String uploadGoodConduct(MultipartFile file) {
        validateFile(file, ALLOWED_PDF_TYPES, MAX_DOCUMENT_SIZE, "Good conduct certificate");

        try {
            String key = "good-conduct/" + UUID.randomUUID() + "-" + sanitizeFilename(file.getOriginalFilename());

            return uploadToS3(file, key);

        } catch (IOException e) {
            throw new RuntimeException("Good conduct certificate upload failed", e);
        }
    }

    /**
     * Upload medical report (PDF only, max 10MB)
     */
    public String uploadMedicalReport(MultipartFile file) {
        validateFile(file, ALLOWED_PDF_TYPES, MAX_DOCUMENT_SIZE, "Medical report");

        try {
            String key = "medical-reports/" + UUID.randomUUID() + "-" + sanitizeFilename(file.getOriginalFilename());

            return uploadToS3(file, key);

        } catch (IOException e) {
            throw new RuntimeException("Medical report upload failed", e);
        }
    }

    /**
     * Generic upload method (backwards compatibility)
     */
    public String upload(MultipartFile file) {
        try {
            String key = "documents/" + UUID.randomUUID() + "-" + sanitizeFilename(file.getOriginalFilename());

            return uploadToS3(file, key);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    /**
     * Validate file against allowed types and size limits
     */
    private void validateFile(MultipartFile file, List<String> allowedTypes, long maxSize, String fileTypeName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(fileTypeName + " cannot be empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    fileTypeName + " must be of type: " + String.join(", ", allowedTypes) +
                            ". Got: " + contentType
            );
        }

        // Validate file size
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    fileTypeName + " size must not exceed " + (maxSize / 1024 / 1024) + "MB. " +
                            "Current size: " + (file.getSize() / 1024 / 1024) + "MB"
            );
        }

        // Validate filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException(fileTypeName + " must have a valid filename");
        }
    }

    /**
     * Upload file to S3/DigitalOcean Spaces
     */
    private String uploadToS3(MultipartFile file, String key) throws IOException {
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
    }

    /**
     * Sanitize filename to prevent path traversal and special characters
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "file";
        }

        // Remove path separators and keep only the filename
        String sanitized = filename.replaceAll("[/\\\\]", "");

        // Replace spaces and special characters (except dots, hyphens, underscores)
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9._-]", "_");

        return sanitized;
    }

    /**
     * Delete a file from S3 (optional utility method)
     */
    public void deleteFile(String fileUrl) {
        try {
            // Extract key from URL
            String key = fileUrl.replace(endpoint + "/", "");

            s3Client.deleteObject(builder -> builder
                    .bucket(bucket)
                    .key(key)
                    .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("File deletion failed", e);
        }
    }
}

//package com.example.marketplace.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class FileUploadService {
//
//    private final S3Client s3Client;
//
//    @Value("${do.spaces.bucket}")
//    private String bucket;
//
//    @Value("${do.spaces.endpoint}")   // e.g. https://yaya-docs.fra1.digitaloceanspaces.com
//    private String endpoint;
//
//    public String upload(MultipartFile file) {
//        try {
//            String key = "documents/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
//
//            s3Client.putObject(
//                    PutObjectRequest.builder()
//                            .bucket(bucket)
//                            .key(key)
//                            .contentType(file.getContentType())
//                            .acl("public-read")
//                            .build(),
//                    RequestBody.fromBytes(file.getBytes())
//            );
//
//            return endpoint + "/" + key;
//
//        } catch (IOException e) {
//            throw new RuntimeException("File upload failed", e);
//        }
//    }
//}
