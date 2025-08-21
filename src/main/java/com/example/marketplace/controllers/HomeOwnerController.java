package com.example.marketplace.controllers;

import com.example.marketplace.dto.HomeOwnerUpdateDTO;
import com.example.marketplace.dto.HomeOwnerUpdateResponseDTO;
import com.example.marketplace.model.HomeOwner;
import com.example.marketplace.service.HomeOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/homeowner")
@RequiredArgsConstructor
public class HomeOwnerController {

    private final HomeOwnerService homeOwnerService;

    @PutMapping("/verify/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HomeOwner> verifyHomeOwner(@PathVariable Long id) {
        return ResponseEntity.ok(homeOwnerService.verifyHomeOwner(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('HOMEOWNER')")
    public ResponseEntity<HomeOwnerUpdateResponseDTO> updateHomeOwner(
            @PathVariable Long id,
            @RequestBody HomeOwnerUpdateDTO dto
    ) {
        return ResponseEntity.ok(homeOwnerService.updateHomeOwner(id, dto));
    }
}
