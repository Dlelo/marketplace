package com.example.marketplace.mapper;

import com.example.marketplace.dto.UserResponseDTO;
import com.example.marketplace.model.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getRoles() != null) {
            dto.setRoles(
                    user.getRoles().stream()
                            .map(role -> {
                                UserResponseDTO.RoleDTO roleDTO = new UserResponseDTO.RoleDTO();
                                roleDTO.setName(role.getName());
                                return roleDTO;
                            })
                            .collect(Collectors.toSet())
            );
        } else {
            dto.setRoles(Collections.emptySet());
        }

        return dto;
    }
}
