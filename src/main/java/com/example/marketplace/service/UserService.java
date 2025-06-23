package com.example.marketplace.service;

import com.example.marketplace.dto.BuyerRegisterRequest;
import com.example.marketplace.dto.SellerRegisterRequest;
import com.example.marketplace.model.Role;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.RoleRepository;
import com.example.marketplace.repository.UserRepository;
import com.example.marketplace.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public User registerBuyer(BuyerRegisterRequest request) {
        Role buyerRole = getRoleByName("BUYER");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(buyerRole))
                .build();

        return userRepository.save(user);
    }

    public User registerSeller(SellerRegisterRequest request) {
        Role sellerRole = getRoleByName("SELLER");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(sellerRole))
                .build();

        return userRepository.save(user);
    }

    public String login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        // Use encoder to match hashed password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    private Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException(roleName + " role does not exist. Please create it first."));
    }
}
