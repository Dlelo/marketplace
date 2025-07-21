package com.example.marketplace.service;

import com.example.marketplace.dto.HomeOwnerRegisterRequest;
import com.example.marketplace.dto.HouseHelpRegisterRequest;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.Role;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.HouseHelpRepository;
import com.example.marketplace.repository.RoleRepository;
import com.example.marketplace.repository.UserRepository;
import com.example.marketplace.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HouseHelpRepository houseHelpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public User registerHomeOwner(HomeOwnerRegisterRequest request) {
        Role buyerRole = getRoleByName("HOMEOWNER");
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(buyerRole))
                .build();
        return userRepository.save(user);
    }

    public User registerHouseHelp(HouseHelpRegisterRequest request) {
        Role sellerRole = getRoleByName("HOUSEHELP");
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(sellerRole))
                .build();
        user = userRepository.save(user);

        HouseHelp houseHelp = new HouseHelp();
        houseHelp.setUser(user);
        houseHelp.setVerified(false);
        houseHelp.setExperienceYears(request.getExperienceYears());
        houseHelp.setSkills(request.getSkills());
        houseHelp.setLanguages(String.join(",", request.getTypes()));
        houseHelp.setAvailability(request.getAvailability());
        houseHelp.setNumberOfChildren(request.getNumberOfChildren());
        houseHelp.setIdNumber(request.getIdNumber());
        houseHelp.setBio(request.getBio() != null ? request.getBio() : "");
        houseHelp.setExpectedSalary(request.getExpectedSalary() != null ? request.getExpectedSalary() : 0.0);
        houseHelp.setPhotoUrl(request.getPhotoUrl() != null ? request.getPhotoUrl() : "");
        houseHelpRepository.save(houseHelp);

        return user;
    }

    public String login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return jwtUtil.generateToken(user.getEmail());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public HouseHelp verifyHouseHelp(Long houseHelpId) {
        HouseHelp houseHelp = houseHelpRepository.findById(houseHelpId)
                .orElseThrow(() -> new RuntimeException("HouseHelp not found"));
        houseHelp.setVerified(true);
        return houseHelpRepository.save(houseHelp);
    }

    private Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException(roleName + " role does not exist. Please create it first."));
    }
}