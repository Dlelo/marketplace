package com.example.marketplace.controllers;

import com.example.marketplace.dto.HomeOwnerUpdateDTO;
import com.example.marketplace.dto.HomeOwnerUpdateResponseDTO;
import com.example.marketplace.dto.SecurityClearanceRequest;
import com.example.marketplace.model.HomeOwner;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.repository.HomeOwnerRepository;
import com.example.marketplace.service.FileUploadService;
import com.example.marketplace.service.HomeOwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/homeowner")
@RequiredArgsConstructor
public class HomeOwnerController {

    private final HomeOwnerService homeOwnerService;
    private final HomeOwnerRepository homeOwnerRepository;
    private final FileUploadService fileUploadService;

    @GetMapping
    public ResponseEntity<Page<HomeOwner>> getAllHomeOwners(Pageable pageable) {
        return ResponseEntity.ok(homeOwnerService.getAllHomeOwners(pageable));
    }

    @PutMapping("/verify/{id}")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<HomeOwner> verifyHomeOwner(@PathVariable Long id) {
        return ResponseEntity.ok(homeOwnerService.verifyHomeOwner(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT','HOMEOWNER','SALES')")
    public ResponseEntity<HomeOwnerUpdateResponseDTO> updateHomeOwner(
            @PathVariable Long id,
            @RequestBody HomeOwnerUpdateDTO dto
    ) {
        return ResponseEntity.ok(homeOwnerService.updateHomeOwner(id, dto));
    }

    @PostMapping("/{id}/upload-national-id")
    public ResponseEntity<?> uploadNationalId(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        HomeOwner homeOwner = homeOwnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

        String url = fileUploadService.upload(file);

        homeOwner.setNationalIdDocument(url);
        homeOwnerRepository.save(homeOwner);

        return ResponseEntity.ok(url);
    }

    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<?> uploadHomeOwnerProfilePicture(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {
            String fileUrl = fileUploadService.uploadProfilePicture(file);

            // TODO: Update database with fileUrl for this home owner
//            homeOwnerService.updateProfilePicture(id, fileUrl);
            HomeOwner homeOwner = homeOwnerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

            homeOwner.setProfilePictureDocument(fileUrl);
            homeOwnerRepository.save(homeOwner);
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("message", "Profile picture uploaded successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT','HOMEOWNER','ADMIN','HOUSEHELP','SECURITY','SALES')")
    public ResponseEntity<HomeOwner> getHomeOwnerById(@PathVariable Long id) {
        HomeOwner homeOwner = homeOwnerService.getHomeOwnerById(id);
        return ResponseEntity.ok(homeOwner);
    }

    @PreAuthorize("hasAnyRole('SECURITY')")
    @PutMapping("/{id}/security-cleared")
    public ResponseEntity<HomeOwner> setSecurityCleared(
            @PathVariable Long id,
            @Valid @RequestBody SecurityClearanceRequest request
    ) {
        return ResponseEntity.ok(
                homeOwnerService.setSecurityCleared(
                        id,
                        request.isCleared(),
                        request.getComments()
                )
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}/active")
    public ResponseEntity<HomeOwner> setActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        return ResponseEntity.ok(
                homeOwnerService.setActiveStatus(id, active)
        );
    }
}
