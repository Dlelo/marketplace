package com.example.marketplace.service;

import com.example.marketplace.dto.HomeOwnerUpdateDTO;
import com.example.marketplace.dto.HouseHelpUpdateDTO;
import com.example.marketplace.dto.RegisterRequest;
import com.example.marketplace.dto.UserResponseDTO;
import com.example.marketplace.model.HomeOwner;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HouseHelpRepository houseHelpRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO registerUser(RegisterRequest dto, String roleName) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        String resolvedRoleName = (roleName == null || roleName.trim().isEmpty())
                ? "HOMEOWNER"
                : roleName.trim().toUpperCase();

        Role role = roleRepository.findByName(resolvedRoleName)
                .orElseThrow(() -> new IllegalArgumentException("Role " + resolvedRoleName + " does not exist. Please create it first."));

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setRoles(new HashSet<>(Collections.singletonList(role)));

        User savedUser = userRepository.save(user);

        if (resolvedRoleName.equalsIgnoreCase("HOUSEHELP")) {
            HouseHelp houseHelp = new HouseHelp();
            houseHelp.setUser(savedUser);
            houseHelp.setVerified(false);
            houseHelpRepository.save(houseHelp);
        }

        return toUserResponseDTO(savedUser);
    }

    public UserResponseDTO addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole = roleRepository.findByName(roleName.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Role> existingRoles = user.getRoles();

        // Exclusive logic for HOUSEHELP and HOMEOWNER
        boolean isHouseHelp = existingRoles.stream().anyMatch(r -> r.getName().equalsIgnoreCase("HOUSEHELP"));
        boolean isHomeOwner = existingRoles.stream().anyMatch(r -> r.getName().equalsIgnoreCase("HOMEOWNER"));

        if (newRole.getName().equalsIgnoreCase("HOUSEHELP") && isHomeOwner) {
            // Replace HOMEOWNER with HOUSEHELP
            existingRoles.removeIf(r -> r.getName().equalsIgnoreCase("HOMEOWNER"));
        } else if (newRole.getName().equalsIgnoreCase("HOMEOWNER") && isHouseHelp) {
            // Replace HOUSEHELP with HOMEOWNER
            existingRoles.removeIf(r -> r.getName().equalsIgnoreCase("HOUSEHELP"));
        }

        existingRoles.add(newRole);

        user.setRoles(existingRoles);
        User updatedUser = userRepository.save(user);

        return toUserResponseDTO(updatedUser);
    }



    private UserResponseDTO toUserResponseDTO(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        return response;
    }


    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setName(user.getName());
                    dto.setRoles(user.getRoles()
                            .stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet()));
                    return dto;
                });
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

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToUserResponseDTO(user);
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRoles(
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );

        dto.setHouseHelp(mapToHouseHelpDTO(user.getHouseHelp()));
        dto.setHomeOwner(mapToHomeOwnerDTO(user.getHomeOwner()));
        return dto;
    }

    private HouseHelpUpdateDTO mapToHouseHelpDTO(HouseHelp houseHelp) {
        if (houseHelp == null) return null;

        HouseHelpUpdateDTO dto = new HouseHelpUpdateDTO();
        dto.setId(houseHelp.getId());
        dto.setNationalId(houseHelp.getNationalId());
        dto.setHomeLocation(houseHelp.getHomeLocation());
        dto.setSkills(houseHelp.getSkills());
        dto.setLanguages(houseHelp.getLanguages());
        return dto;
    }

    private HomeOwnerUpdateDTO mapToHomeOwnerDTO(HomeOwner homeOwner) {
        if (homeOwner == null) return null;

        HomeOwnerUpdateDTO dto = new HomeOwnerUpdateDTO();
        dto.setId(homeOwner.getId());
        dto.setNationalId(homeOwner.getNationalId());
        dto.setHomeLocation(homeOwner.getHomeLocation());
        dto.setPhoneNumber(homeOwner.getPhoneNumber());
        return dto;
    }


}