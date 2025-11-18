package com.example.marketplace.dto;

import lombok.Data;
import java.util.List;

@Data
public class EditUserRolesRequestDTO {
    private Long userId;
    private List<String> roles;
}
