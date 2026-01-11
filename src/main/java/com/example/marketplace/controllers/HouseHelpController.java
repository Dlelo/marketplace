package com.example.marketplace.controllers;

import com.example.marketplace.dto.HouseHelpFilterDTO;
import com.example.marketplace.dto.HouseHelpUpdateDTO;
import com.example.marketplace.dto.HouseHelpUpdateResponseDTO;
import com.example.marketplace.dto.HouseHelpVerificationResponseDTO;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.repository.HouseHelpRepository;
import com.example.marketplace.service.FileUploadService;
import com.example.marketplace.service.HouseHelpService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/househelp")
@RequiredArgsConstructor
public class HouseHelpController {
    private final HouseHelpService houseHelpService;
    private final HouseHelpRepository houseHelpRepository;
    private final FileUploadService fileUploadService;

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('AGENT','HOMEOWNER','ADMIN','SALES','SECURITY')")
    public ResponseEntity<Page<HouseHelp>> searchHouseHelps(
            @RequestBody HouseHelpFilterDTO filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(houseHelpService.findByFilterAndPage(filter, pageable));
    }

    @PutMapping("/verify/{id}")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<HouseHelpVerificationResponseDTO> verifyHouseHelp(@PathVariable Long houseHelpId) {
        return ResponseEntity.ok(houseHelpService.verifyHouseHelp(houseHelpId));
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN','HOUSEHELP','SALES')")
    public HouseHelpUpdateResponseDTO updateHouseHelp(@PathVariable Long id, @RequestBody HouseHelpUpdateDTO dto) {
        return houseHelpService.updateHouseHelp(id, dto);
    }

    @PostMapping("/{id}/upload-national-id")
    public ResponseEntity<?> uploadNationalId(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        HouseHelp houseHelp = houseHelpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HouseHelp not found"));

        String url = fileUploadService.upload(file);

        houseHelp.setNationalIdDocument(url);
        houseHelpRepository.save(houseHelp);

        return ResponseEntity.ok(url);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT','HOMEOWNER','ADMIN','HOUSEHELP','SECURITY','SALES')")
    public ResponseEntity<HouseHelp> getHouseHelpById(@PathVariable Long id) {
        HouseHelp houseHelp = houseHelpService.getHouseHelpById(id);
        return ResponseEntity.ok(houseHelp);
    }

    @PreAuthorize("hasAnyRole('SECURITY')")
    @PutMapping("/{id}/security-cleared")
    public ResponseEntity<HouseHelp> setSecurityCleared(
            @PathVariable Long id,
            @RequestParam boolean cleared) {

        return ResponseEntity.ok(
                houseHelpService.setSecurityCleared(id, cleared)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}/active")
    public ResponseEntity<HouseHelp> setActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        return ResponseEntity.ok(
                houseHelpService.setActiveStatus(id, active)
        );
    }

}