package com.example.marketplace.service;

import com.example.marketplace.model.Role;
import com.example.marketplace.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role createRole(Role role) {
        if(roleRepository.findByName(role.getName()).isPresent()) {
            throw new IllegalArgumentException("Role name already exists");
        }
        return roleRepository.save(role);
    }

    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        roleRepository.delete(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

}
