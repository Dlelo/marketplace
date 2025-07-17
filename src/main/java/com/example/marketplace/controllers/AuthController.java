package com.example.marketplace.controllers;

import com.example.marketplace.dto.HomeOwnerRegisterRequest;
import com.example.marketplace.dto.HouseHelpRegisterRequest;
import com.example.marketplace.dto.LoginRequest;
import com.example.marketplace.model.User;
import com.example.marketplace.security.JWTUtil;
import com.example.marketplace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register/homeowner")
    public ResponseEntity<User> registerHomeOwner(@RequestBody HomeOwnerRegisterRequest dto) {
        return ResponseEntity.ok(userService.registerHomeOwner(dto));
    }

    @PostMapping("/register/househelp")
    public ResponseEntity<User> registerHouseHelp(@RequestBody HouseHelpRegisterRequest dto) {
        return ResponseEntity.ok(userService.registerHouseHelp(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("EMAIL: " + loginRequest.getEmail());
            System.out.println("PASSWORD: " + loginRequest.getPassword());
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest().body("Email and password are required.");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            System.out.println("AUTHENTICATION SUCCESS: " + authentication.getName());

            String token = jwtUtil.generateToken(authentication.getName());
            System.out.println("JWT GENERATED: " + token);
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}

