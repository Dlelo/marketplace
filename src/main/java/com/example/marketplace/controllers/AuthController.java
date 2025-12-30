package com.example.marketplace.controllers;

import com.example.marketplace.dto.LoginRequest;
import com.example.marketplace.dto.RegisterRequest;
import com.example.marketplace.dto.UserResponseDTO;
import com.example.marketplace.model.User;
import com.example.marketplace.security.CustomUserDetails;
import com.example.marketplace.security.JWTUtil;
import com.example.marketplace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
            if (loginRequest.getIdentifier() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body("Email or phone number and password are required.");
            }

            String identifier = loginRequest.getIdentifier();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            identifier,
                            loginRequest.getPassword()
                    )
            );

            Object principal = authentication.getPrincipal();
            Long userId = null;
            String role = null;
            String email = authentication.getName();

            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                User user = userDetails.getUser();
                userId = user.getId();
                role = extractRoleFromUser(user);
            } else {
               User user = userService.findByEmailOrPhone(identifier);
                userId = user.getId();
                role = extractRoleFromUser(user);
            }

            User user = userService.findByEmailOrPhone(identifier);

            // 4️⃣ Build UserDetails for JWT
            org.springframework.security.core.userdetails.User userDetails =
                    new org.springframework.security.core.userdetails.User(
                            user.getEmail() != null && !user.getEmail().isBlank()
                                    ? user.getEmail()
                                    : user.getPhoneNumber(),
                            user.getPassword(),
                            user.getRoles()
                                    .stream()
                                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                                    .collect(Collectors.toList())
                    );

            // 5️⃣ Generate JWT
            String token = jwtUtil.generateToken(userDetails, user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", userId);
            response.put("role", role);

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred" + e.getMessage());
        }
    }

    private String extractRoleFromUser(User user) {
        if (user.getRoles() == null || user.getRoles().toString().isEmpty()) {
            return "HOMEOWNER";
        }
        return user.getRoles().toString().replace("ROLE_", "");
    }
}