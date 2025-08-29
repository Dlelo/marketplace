package com.example.marketplace.controllers;

import com.example.marketplace.dto.HireRequestDTO;
import com.example.marketplace.dto.HireRequestResponseDTO;
import com.example.marketplace.enums.RequestStatus;
import com.example.marketplace.model.HireRequest;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.repository.HouseHelpRepository;
import com.example.marketplace.service.HireRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hire-requests")
@RequiredArgsConstructor
public class HireRequestController {
    private final HireRequestService hireRequestService;
    private final HouseHelpRepository houseHelpRepository;

    @GetMapping
    public ResponseEntity<Page<HireRequest>> getAllHireRequests(Pageable pageable) {
        return ResponseEntity.ok(hireRequestService.getAllHireRequests(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HOMEOWNER','AGENT','ADMIN')")
    public ResponseEntity<HireRequestResponseDTO> createHireRequest(@RequestBody HireRequestDTO hireRequestDTO, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(hireRequestService.createHireRequest(hireRequestDTO, hireRequestService.findHouseOwnerByEmail(email)));
    }

    @PostMapping("/househelp/{houseHelpId}/verify")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<String> verifyHouseHelp(@PathVariable Long houseHelpId) {
        HouseHelp houseHelp = houseHelpRepository.findById(houseHelpId)
                .orElseThrow(() -> new RuntimeException("HouseHelp not found"));
        if (houseHelp.isVerified()) {
            return ResponseEntity.ok("HouseHelp is already verified");
        }
        houseHelp.setVerified(true);
        houseHelpRepository.save(houseHelp);
        return ResponseEntity.ok("HouseHelp verified successfully");
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HOUSEHELP')")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody RequestStatus status) {
        hireRequestService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/househelp/{houseHelpId}")
    @PreAuthorize("hasRole('HOUSEHELP')")
    public ResponseEntity<List<HireRequestResponseDTO>> getRequestsForHouseHelp(@PathVariable Long houseHelpId) {
        return ResponseEntity.ok(hireRequestService.getRequestsForHouseHelp(houseHelpId));
    }

    @GetMapping("/homeowner/{houseOwnerId}")
    @PreAuthorize("hasRole('HOMEOWNER')")
    public ResponseEntity<List<HireRequestResponseDTO>> getRequestsForHouseOwner(@PathVariable Long houseOwnerId) {
        return ResponseEntity.ok(hireRequestService.findByHouseOwner_Id(houseOwnerId));
    }
}