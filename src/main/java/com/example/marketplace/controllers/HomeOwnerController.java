package com.example.marketplace.controllers;

import com.example.marketplace.dto.HomeOwnerUpdateDTO;
import com.example.marketplace.dto.HomeOwnerUpdateResponseDTO;
import com.example.marketplace.model.HomeOwner;
import com.example.marketplace.service.HomeOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/homeowner")
@RequiredArgsConstructor
public class HomeOwnerController {

    private final HomeOwnerService homeOwnerService;

    @GetMapping
    public ResponseEntity<List<HomeOwner>> getAllHomeOwners() {
        return ResponseEntity.ok(homeOwnerService.getAllHomeOwners());
    }

    @PutMapping("/verify/{id}")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<HomeOwner> verifyHomeOwner(@PathVariable Long id) {
        return ResponseEntity.ok(homeOwnerService.verifyHomeOwner(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT','HOMEOWNER')")
    public ResponseEntity<HomeOwnerUpdateResponseDTO> updateHomeOwner(
            @PathVariable Long id,
            @RequestBody HomeOwnerUpdateDTO dto
    ) {
        return ResponseEntity.ok(homeOwnerService.updateHomeOwner(id, dto));
    }
}
