package com.example.marketplace.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String phoneNumber;
    private String name;
    private Set<String> roles;
    private HouseHelpResponseDTO houseHelp;
    private HomeOwnerUpdateDTO homeOwner;
    private AgentProfileDTO agentProfile;
    /** ID of the user (typically an agent) who created this account. */
    private Long createdById;
    private String createdByName;

    @Data
    public static class AgentProfileDTO {
        private Long id;
        private String fullName;
        private String phoneNumber;
        private String email;
        private String nationalId;
        private String locationOfOperation;
        private String homeLocation;
        private String houseNumber;
        private boolean verified;
        /** ADMIN or EMPLOYEE */
        private String agentRole;
        private Long agencyId;
        private String agencyName;
        private Boolean agencyVerified;
    }
}