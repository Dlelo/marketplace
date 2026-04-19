package com.example.marketplace.service;

import com.example.marketplace.dto.*;
import com.example.marketplace.enums.AgentRole;
import com.example.marketplace.model.*;
import com.example.marketplace.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AgentRepository agentRepository;
    private final UserRepository userRepository;

    public Page<Agent> getAllAgents(Pageable pageable) {
        return agentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getUsersWithAgentRole(Pageable pageable) {
        return userRepository.findDistinctByRoles_Name("AGENT", pageable)
                .map(this::toUserDTO);
    }

    private UserResponseDTO toUserDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        agentRepository.findByUser(user).ifPresent(agent -> {
            dto.setAgentProfile(buildAgentProfile(agent));
        });
        return dto;
    }

    public AgentUpdateResponseDTO updateAgent(Long id, AgentUpdateDTO dto) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        if (dto.getFullName() != null) agent.setFullName(dto.getFullName());
        if (dto.getNationalId() != null) agent.setNationalId(dto.getNationalId());
        if (dto.getPhoneNumber() != null) agent.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getEmail() != null) agent.setEmail(dto.getEmail());
        if (dto.getHomeLocation() != null) agent.setHomeLocation(dto.getHomeLocation());
        if (dto.getLocationOfOperation() != null) agent.setLocationOfOperation(dto.getLocationOfOperation());
        if (dto.getHouseNumber() != null) agent.setHouseNumber(dto.getHouseNumber());
        if (dto.getIdDocument() != null) agent.setIdDocument(dto.getIdDocument());
        if (dto.getAgentRole() != null) {
            try {
                agent.setAgentRole(AgentRole.valueOf(dto.getAgentRole().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        Agent updated = agentRepository.save(agent);
        List<String> missingFields = getMissingFields(updated);
        return new AgentUpdateResponseDTO(updated, missingFields);
    }

    public Agent verifyAgent(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        List<String> missingFields = getMissingFields(agent);
        if (!missingFields.isEmpty()) {
            throw new RuntimeException("Profile incomplete. Missing fields: " + String.join(", ", missingFields));
        }

        agent.setVerified(true);
        return agentRepository.save(agent);
    }

    /** Get a single agent's profile (returns the linked user's full DTO) */
    public UserResponseDTO getAgentProfile(Long agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        User user = agent.getUser();

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        dto.setAgentProfile(buildAgentProfile(agent));
        return dto;
    }

    // ─────────────────────────────────────────────────
    //  Private helpers
    // ─────────────────────────────────────────────────

    private UserResponseDTO.AgentProfileDTO buildAgentProfile(Agent agent) {
        UserResponseDTO.AgentProfileDTO profile = new UserResponseDTO.AgentProfileDTO();
        profile.setId(agent.getId());
        profile.setFullName(agent.getFullName());
        profile.setPhoneNumber(agent.getPhoneNumber());
        profile.setEmail(agent.getEmail());
        profile.setNationalId(agent.getNationalId());
        profile.setLocationOfOperation(agent.getLocationOfOperation());
        profile.setHomeLocation(agent.getHomeLocation());
        profile.setHouseNumber(agent.getHouseNumber());
        profile.setVerified(agent.isVerified());
        profile.setAgentRole(agent.getAgentRole() != null ? agent.getAgentRole().name() : AgentRole.ADMIN.name());
        if (agent.getAgency() != null) {
            profile.setAgencyId(agent.getAgency().getId());
            profile.setAgencyName(agent.getAgency().getName());
            profile.setAgencyVerified(agent.getAgency().isVerified());
        }
        return profile;
    }

    private List<String> getMissingFields(Agent agent) {
        List<String> missing = new ArrayList<>();
        if (isBlank(agent.getFullName())) missing.add("fullName");
        if (isBlank(agent.getNationalId())) missing.add("nationalId");
        if (isBlank(agent.getPhoneNumber())) missing.add("phoneNumber");
        if (isBlank(agent.getEmail())) missing.add("email");
        if (isBlank(agent.getHomeLocation())) missing.add("homeLocation");
        if (isBlank(agent.getLocationOfOperation())) missing.add("locationOfOperation");
        if (agent.getHouseNumber() == null) missing.add("houseNumber");
        if (isBlank(agent.getIdDocument())) missing.add("idDocument");
        return missing;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
