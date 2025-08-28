package com.example.marketplace.controllers;

import com.example.marketplace.dto.HouseHelpFilterDTO;
import com.example.marketplace.dto.HouseHelpUpdateDTO;
import com.example.marketplace.dto.HouseHelpUpdateResponseDTO;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.service.HouseHelpService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<HouseHelp>> getAllHouseHelps() {
        return ResponseEntity.ok(houseHelpService.getAllHouseHelps());
    }

    @PutMapping("/verify/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HouseHelp> verifyHouseHelp(@PathVariable Long id) {
        return ResponseEntity.ok(houseHelpService.verifyHouseHelp(id));
    }

    @PostMapping("/filter")
    @PreAuthorize("hasRole('HOMEOWNER')")
    public ResponseEntity<List<HouseHelp>> filterHouseHelps(@RequestBody HouseHelpFilterDTO filter) {
        return ResponseEntity.ok(houseHelpService.findByFilter(filter));
    }

    @PatchMapping("/{id}")
    public HouseHelpUpdateResponseDTO updateHouseHelp(@PathVariable Long id, @RequestBody HouseHelpUpdateDTO dto) {
        return houseHelpService.updateHouseHelp(id, dto);
    }
}