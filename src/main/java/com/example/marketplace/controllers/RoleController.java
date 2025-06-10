package com.example.marketplace.controllers;

import com.example.marketplace.model.Role;
import com.example.marketplace.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        try {
            Role savedRole = roleService.createRole(role);
            return ResponseEntity.ok(savedRole);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
