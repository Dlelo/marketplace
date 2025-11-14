package com.example.marketplace.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private Set<String> roles;
    private HouseHelpResponseDTO houseHelp;
    private HomeOwnerUpdateDTO homeOwner;
}