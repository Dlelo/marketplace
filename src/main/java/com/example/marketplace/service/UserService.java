package com.example.marketplace.service;

import com.example.marketplace.dto.RegisterRequest;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.Role;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.HouseHelpRepository;
import com.example.marketplace.repository.RoleRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HouseHelpRepository houseHelpRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest dto, String roleName) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role " + roleName + " does not exist. Please create it first."));

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        User savedUser = userRepository.save(user);

        if (roleName.equals("HOUSEHELP")) {
            HouseHelp houseHelp = new HouseHelp();
            houseHelp.setUser(savedUser);
            houseHelp.setVerified(false);
            houseHelpRepository.save(houseHelp);
        }

        return savedUser;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public HouseHelp verifyHouseHelp(Long houseHelpId) {
        HouseHelp houseHelp = houseHelpRepository.findById(houseHelpId)
                .orElseThrow(() -> new RuntimeException("HouseHelp not found"));
        houseHelp.setVerified(true);
        return houseHelpRepository.save(houseHelp);
    }
}