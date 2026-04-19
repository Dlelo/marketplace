package com.example.marketplace.dto;

import lombok.Data;
import java.util.List;

/** Full Agency details returned to the client, including its member list. */
@Data
public class AgencyResponseDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String locationOfOperation;
    private String homeLocation;
    private String houseNumber;
    private boolean verified;
    private List<AgencyMemberDTO> members;
}
