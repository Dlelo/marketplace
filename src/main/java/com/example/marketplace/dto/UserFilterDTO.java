package com.example.marketplace.dto;

import com.example.marketplace.enums.AvailabilityType;
import com.example.marketplace.enums.HouseHelpStatus;
import com.example.marketplace.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilterDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private Set<String> roles;
}


