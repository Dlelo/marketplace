package com.example.marketplace.dto;

import com.example.marketplace.model.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AgentUpdateResponseDTO {
    private Agent updatedAgent;
    private List<String> missingFields;
}
