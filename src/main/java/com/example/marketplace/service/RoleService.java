package com.example.marketplace.service;

import com.example.marketplace.dto.RoleResponseDTO;
import com.example.marketplace.model.Role;
import com.example.marketplace.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleResponseDTO createRole(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new IllegalArgumentException("Role " + role.getName() + " already exists");
        }
        Role savedRole = roleRepository.save(role);
        RoleResponseDTO response = new RoleResponseDTO();
        response.setId(savedRole.getId());
        response.setName(savedRole.getName());
        response.setDescription(savedRole.getDescription());
        return response;
    }

    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new IllegalArgumentException("Role with ID " + roleId + " does not exist");
        }
        roleRepository.deleteById(roleId);
    }

    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> {
                    RoleResponseDTO dto = new RoleResponseDTO();
                    dto.setId(role.getId());
                    dto.setName(role.getName());
                    dto.setDescription(role.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}