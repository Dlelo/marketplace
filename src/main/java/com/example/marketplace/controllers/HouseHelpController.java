package com.example.marketplace.controllers;

import com.example.marketplace.dto.HouseHelpFilterDTO;
import com.example.marketplace.dto.HouseHelpUpdateDTO;
import com.example.marketplace.dto.HouseHelpUpdateResponseDTO;
import com.example.marketplace.dto.HouseHelpVerificationResponseDTO;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.service.HouseHelpService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/househelp")
@RequiredArgsConstructor
public class HouseHelpController {
    private final HouseHelpService houseHelpService;

    @GetMapping
    public ResponseEntity<Page<HouseHelp>> getAllHouseHelps(Pageable pageable) {
        return ResponseEntity.ok(houseHelpService.getAllHouseHelps(pageable));
    }

    @PutMapping("/verify/{id}")
    @PostMapping("/househelp/{houseHelpId}/verify")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<HouseHelpVerificationResponseDTO> verifyHouseHelp(@PathVariable Long houseHelpId) {
        return ResponseEntity.ok(houseHelpService.verifyHouseHelp(houseHelpId));
    }

    @PostMapping("/filter")
    @PreAuthorize("hasAnyRole('AGENT','HOMEOWNER','ADMIN')")
    public ResponseEntity<List<HouseHelp>> filterHouseHelps(@RequestBody HouseHelpFilterDTO filter) {
        return ResponseEntity.ok(houseHelpService.findByFilter(filter));
    }

    @PatchMapping("/{id}")
    public HouseHelpUpdateResponseDTO updateHouseHelp(@PathVariable Long id, @RequestBody HouseHelpUpdateDTO dto) {
        return houseHelpService.updateHouseHelp(id, dto);
    }
}