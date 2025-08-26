package com.example.marketplace.service;

import com.example.marketplace.dto.AgentUpdateDTO;
import com.example.marketplace.dto.AgentUpdateResponseDTO;
import com.example.marketplace.model.Agent;
import com.example.marketplace.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AgentRepository agentRepository;

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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
