package com.example.marketplace.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Instant createdAt;
    private Set<RoleDTO> roles;
    private HouseHelpResponseDTO houseHelp;
    private HomeOwnerUpdateDTO homeOwner;

    @Data
    public static class RoleDTO {
        private String name;
    }
}
