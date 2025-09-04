package com.example.marketplace.controllers;

import com.example.marketplace.dto.HireRequestDTO;
import com.example.marketplace.dto.HireRequestResponseDTO;
import com.example.marketplace.dto.HouseHelpVerificationResponseDTO;
import com.example.marketplace.enums.RequestStatus;
import com.example.marketplace.model.HireRequest;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.service.HireRequestService;
import com.example.marketplace.service.HouseHelpService;
import com.example.marketplace.service.PaymentService;
import com.example.marketplace.service.SubscriptionService;
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
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final HouseHelpService houseHelpService;

    @GetMapping
    public ResponseEntity<Page<HireRequest>> getAllHireRequests(Pageable pageable) {
        return ResponseEntity.ok(hireRequestService.getAllHireRequests(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HOMEOWNER','AGENT','ADMIN')")
    public ResponseEntity<?> createHireRequest(@RequestBody HireRequestDTO hireRequestDTO, Authentication authentication) {
        String email = authentication.getName();
        boolean hasActiveSubscription = subscriptionService.hasActiveSubscription(email);
        boolean hasSuccessfulPayment = paymentService.hasSuccessfulPayment(email);

        if (!hasActiveSubscription && !hasSuccessfulPayment) {
            return ResponseEntity
                    .status(402)
                    .body("Active subscription or successful payment required to hire a househelp.");
        }

        HireRequestResponseDTO response = hireRequestService.createHireRequest(hireRequestDTO, email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/househelp/{houseHelpId}/verify")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<HouseHelpVerificationResponseDTO> verifyHouseHelp(@PathVariable Long houseHelpId) {
        return ResponseEntity.ok(houseHelpService.verifyHouseHelp(houseHelpId));
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

    @GetMapping("/homeowner/{homeOwnerId}")
    @PreAuthorize("hasRole('HOMEOWNER')")
    public ResponseEntity<List<HireRequestResponseDTO>> getRequestsForHomeOwner(@PathVariable Long homeOwnerId) {
        return ResponseEntity.ok(hireRequestService.findByHomeOwner_Id(homeOwnerId));
    }
}
