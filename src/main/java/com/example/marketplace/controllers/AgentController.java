package com.example.marketplace.controllers;

import com.example.marketplace.dto.AgentUpdateDTO;
import com.example.marketplace.dto.AgentUpdateResponseDTO;
import com.example.marketplace.model.Agent;
import com.example.marketplace.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor

public class AgentController {
    private final AgentService agentService;

    @GetMapping
    public ResponseEntity<Page<Agent>> getAllAgents(Pageable pageable) {
        return ResponseEntity.ok(agentService.getAllAgents(pageable));
    }

    @PutMapping("/verify/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Agent> verifyAgent(@PathVariable Long id) {
        return ResponseEntity.ok(agentService.verifyAgent(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<AgentUpdateResponseDTO> updateAgent(
            @PathVariable Long id,
            @RequestBody AgentUpdateDTO dto
    ) {
        return ResponseEntity.ok(agentService.updateAgent(id, dto));
    }
}