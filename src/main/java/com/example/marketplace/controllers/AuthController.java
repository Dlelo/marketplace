package com.example.marketplace.controllers;

import com.example.marketplace.dto.LoginRequest;
import com.example.marketplace.dto.RegisterRequest;
import com.example.marketplace.dto.UserResponseDTO;
import com.example.marketplace.security.JWTUtil;
import com.example.marketplace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping(value = {"/register", "/register/{role}"})
    public ResponseEntity<?> register(@RequestBody RegisterRequest dto, @PathVariable(required = false) String role) {
        try {
            String roleName = (role == null || role.trim().isEmpty()) ? "HOMEOWNER" : role.toUpperCase();
            if (!roleName.equals("HOMEOWNER") && !roleName.equals("HOUSEHELP") &&
                    !roleName.equals("ADMIN") && !roleName.equals("AGENT")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid role: must be HOMEOWNER, HOUSEHELP, ADMIN, or AGENT");
            }

            if (roleName.equals("ADMIN") || roleName.equals("AGENT")) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Only ADMIN can register ADMIN or AGENT");
                }
            } else if (roleName.equals("HOUSEHELP")) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_AGENT"))) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Only ADMIN or AGENT can register HOUSEHELP");
                }
            }

            UserResponseDTO userResponse = userService.registerUser(dto, roleName);
            return ResponseEntity.ok(userResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest().body("Email and password are required.");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            String token = jwtUtil.generateToken(authentication.getName());
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}