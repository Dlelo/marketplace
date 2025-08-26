package com.example.marketplace.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class RoleRequestDTO {
    @NonNull
    private String name;
    private String description;
}