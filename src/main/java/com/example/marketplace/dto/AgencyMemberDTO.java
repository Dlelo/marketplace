package com.example.marketplace.dto;

import lombok.Data;

/** A single member (Agent user) of an Agency. */
@Data
public class AgencyMemberDTO {
    private Long id;          // Agent entity ID
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String agentRole; // ADMIN or EMPLOYEE
    private boolean verified;
}
