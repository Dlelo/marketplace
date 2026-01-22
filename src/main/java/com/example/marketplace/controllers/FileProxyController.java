package com.example.marketplace.controllers;

import com.example.marketplace.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Optional: Endpoint to serve private files through your backend
 * This allows you to add authentication/authorization checks
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")


public class FileProxyController {

    private final FileUploadService fileUploadService;

    /**
     * Proxy endpoint to serve private files with authentication
     * GET /api/files/proxy?url=https://...
     *
     * Frontend calls this endpoint, backend fetches from S3 with credentials,
     * then streams the file back to the frontend
     */
    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxyPrivateFile(
            @RequestParam String url,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            // TODO: Add authentication check here
            // if (!isUserAuthorized(authHeader)) {
            //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            // }

            // Fetch file from S3
            byte[] fileContent = fileUploadService.fetchFile(url);

            // Determine content type from URL
            String contentType = determineContentType(url);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600")
                    .body(fileContent);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String determineContentType(String url) {
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerUrl.endsWith(".png")) {
            return "image/png";
        } else if (lowerUrl.endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/octet-stream";
    }
}

