package com.example.marketplace.controllers;

import com.example.marketplace.dto.HireRequestDTO;
import com.example.marketplace.enums.RequestStatus;
import com.example.marketplace.model.HireRequest;
import com.example.marketplace.service.HireRequestService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    @PreAuthorize("hasRole('HOMEOWNER')")
    public ResponseEntity<HireRequest> createHireRequest(@RequestBody HireRequestDTO hireRequestDTO, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(hireRequestService.createHireRequest(hireRequestDTO, hireRequestService.findHouseOwnerByEmail(email)));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HOUSEHELP')")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody RequestStatus status) {
        hireRequestService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/househelp/{houseHelpId}")
    @PreAuthorize("hasRole('HOUSEHELP')")
    public ResponseEntity<List<HireRequest>> getRequestsForHouseHelp(@PathVariable Long houseHelpId) {
        return ResponseEntity.ok(hireRequestService.getRequestsForHouseHelp(houseHelpId));
    }

    @GetMapping("/homeowner/{houseOwnerId}")
    @PreAuthorize("hasRole('HOMEOWNER')")
    public ResponseEntity<List<HireRequest>> getRequestsForHouseOwner(@PathVariable Long houseOwnerId) {
        return ResponseEntity.ok(hireRequestService.findByHouseOwner_Id(houseOwnerId));
    }
}