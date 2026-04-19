package com.example.marketplace.controllers;

import com.example.marketplace.dto.*;
import com.example.marketplace.service.AgencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agency")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    // ── Agency CRUD ───────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AgencyResponseDTO> createAgency(@RequestBody AgencyDTO dto) {
        return ResponseEntity.ok(agencyService.createAgency(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'SALES', 'SECURITY')")
    public ResponseEntity<Page<AgencyResponseDTO>> getAllAgencies(Pageable pageable) {
        return ResponseEntity.ok(agencyService.getAllAgencies(pageable));
    }

    /** Agent retrieves their own agency (derived from their login identity). */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<AgencyResponseDTO> getMyAgency(Authentication authentication) {
        return ResponseEntity.ok(agencyService.getMyAgency(authentication.getName()));
    }

    @GetMapping("/{agencyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<AgencyResponseDTO> getAgency(@PathVariable Long agencyId) {
        return ResponseEntity.ok(agencyService.getAgency(agencyId));
    }

    @PatchMapping("/{agencyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<AgencyResponseDTO> updateAgency(
            @PathVariable Long agencyId,
            @RequestBody AgencyDTO dto) {
        return ResponseEntity.ok(agencyService.updateAgency(agencyId, dto));
    }

    @PutMapping("/{agencyId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AgencyResponseDTO> verifyAgency(@PathVariable Long agencyId) {
        return ResponseEntity.ok(agencyService.verifyAgency(agencyId));
    }

    // ── Members ───────────────────────────────────────

    @PostMapping("/{agencyId}/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<AgencyResponseDTO> addMember(
            @PathVariable Long agencyId,
            @RequestBody AddMemberDTO dto) {
        return ResponseEntity.ok(agencyService.addMember(agencyId, dto));
    }

    @DeleteMapping("/{agencyId}/members/{agentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long agencyId,
            @PathVariable Long agentId) {
        agencyService.removeMember(agencyId, agentId);
        return ResponseEntity.noContent().build();
    }

    // ── Househelps ────────────────────────────────────

    @GetMapping("/{agencyId}/househelps")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<AgentHouseHelpDTO>> getHouseHelps(@PathVariable Long agencyId) {
        return ResponseEntity.ok(agencyService.getHouseHelps(agencyId));
    }

    @PostMapping("/{agencyId}/househelps/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<UserResponseDTO> registerHouseHelp(
            @PathVariable Long agencyId,
            @RequestBody RegisterRequest dto) {
        return ResponseEntity.ok(agencyService.registerAndAssignHouseHelp(agencyId, dto));
    }

    // ── Earnings ──────────────────────────────────────

    @GetMapping("/{agencyId}/earnings")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<AgentEarningsDTO> getEarnings(@PathVariable Long agencyId) {
        return ResponseEntity.ok(agencyService.getEarnings(agencyId));
    }

    // ── Withdrawals ───────────────────────────────────

    @PostMapping("/{agencyId}/withdrawal")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<WithdrawalRequestResponseDTO> requestWithdrawal(
            @PathVariable Long agencyId,
            @RequestBody WithdrawalRequestDTO dto) {
        return ResponseEntity.ok(agencyService.requestWithdrawal(agencyId, dto));
    }

    @GetMapping("/{agencyId}/withdrawals")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<WithdrawalRequestResponseDTO>> getWithdrawals(@PathVariable Long agencyId) {
        return ResponseEntity.ok(agencyService.getWithdrawals(agencyId));
    }
}
