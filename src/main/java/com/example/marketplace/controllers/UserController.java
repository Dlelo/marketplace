package com.example.marketplace.controllers;

import com.example.marketplace.dto.*;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.User;
import com.example.marketplace.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<Page<UserResponseDTO>> listUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<User>> searchUser(
            @RequestBody UserFilterDTO filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.findByFilterAndPage(filter, pageable));
    }

    @PatchMapping("/roles")
    public ResponseEntity<?> addRole(@RequestBody AddRoleToUserRequestDTO request) {
        try {
            UserResponseDTO updatedUser = userService.addRoleToUser(
                    request.getUserId(),
                    request.getRoleName()
            );
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/roles/edit")
    public ResponseEntity<?> editUserRoles(@RequestBody EditUserRolesRequestDTO request) {
        try {
            UserResponseDTO updatedUser = userService.updateUserRoles(
                    request.getUserId(),
                    request.getRoles()
            );
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'HOUSEHELP', 'HOMEOWNER')")

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserResponseDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest request) {

        UserResponseDTO updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }
}
