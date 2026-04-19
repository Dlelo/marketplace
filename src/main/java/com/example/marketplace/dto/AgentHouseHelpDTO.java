package com.example.marketplace.dto;

import lombok.Data;

@Data
public class AgentHouseHelpDTO {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private boolean verified;
    private boolean active;
    private String hiringStatus;
}
