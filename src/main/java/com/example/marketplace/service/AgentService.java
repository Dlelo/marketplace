package com.example.marketplace.service;

import com.example.marketplace.dto.AgentUpdateDTO;
import com.example.marketplace.dto.AgentUpdateResponseDTO;
import com.example.marketplace.dto.UserResponseDTO;
import com.example.marketplace.model.Agent;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.Role;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.AgentRepository;
import com.example.marketplace.repository.HouseHelpRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AgentRepository agentRepository;
    private final HouseHelpRepository houseHelpRepository;
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
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()));
        // attach agent profile if it exists
        agentRepository.findByUser(user).ifPresent(agent -> {
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
            dto.setAgentProfile(profile);
        });
        return dto;
    }

    public AgentUpdateResponseDTO updateAgent(Long id, AgentUpdateDTO dto) {
        Agent agent =agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        if (dto.getFullName() != null) agent.setFullName(dto.getFullName());
        if (dto.getNationalId() != null) agent.setNationalId(dto.getNationalId());
        if (dto.getPhoneNumber() != null) agent.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getEmail() != null) agent.setEmail(dto.getEmail());
        if (dto.getHomeLocation() != null) agent.setHomeLocation(dto.getHomeLocation());
        if (dto.getLocationOfOperation() != null) agent.setLocationOfOperation(dto.getLocationOfOperation());
        if (dto.getHouseNumber() != null) agent.setHouseNumber(dto.getHouseNumber());
        if (dto.getIdDocument() != null) agent.setIdDocument(dto.getIdDocument());

        Agent updated = agentRepository.save(agent);

        List<String> missingFields = getMissingFields(updated);

        return new AgentUpdateResponseDTO(updated, missingFields);
    }

    public Agent verifyAgent(Long id) {
        Agent agent =agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        List<String> missingFields = getMissingFields(agent);

        if (!missingFields.isEmpty()) {
            throw new RuntimeException("Profile incomplete. Missing fields: " + String.join(", ", missingFields));
        }

        agent.setVerified(true);
        return agentRepository.save(agent);
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

    public void assignHouseHelpToAgent(Long agentId, Long househelpId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        HouseHelp houseHelp = houseHelpRepository.findById(househelpId)
                .orElseThrow(() -> new RuntimeException("HouseHelp not found"));
        houseHelp.setAgent(agent);
        houseHelpRepository.save(houseHelp);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
