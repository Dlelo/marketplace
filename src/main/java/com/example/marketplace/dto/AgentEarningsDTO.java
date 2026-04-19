package com.example.marketplace.dto;

import lombok.Data;

import java.util.List;

@Data
public class AgentEarningsDTO {
    private Long agentId;
    private int totalHires;
    private double totalEarned;
    private double totalWithdrawn;
    private double balanceRemaining;
    private List<AgentHireRequestDTO> hireRequests;
    private List<WithdrawalRequestResponseDTO> withdrawals;
}
