package com.example.marketplace.controllers;

import com.example.marketplace.dto.AddRoleToUserRequestDTO;
import com.example.marketplace.dto.UserResponseDTO;
import com.example.marketplace.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/user/roles")
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDTO>> listUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }
}
