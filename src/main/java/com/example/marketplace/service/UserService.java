package com.example.marketplace.service;

import com.example.marketplace.dto.*;
import com.example.marketplace.model.HomeOwner;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.Role;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.HomeOwnerRepository;
import com.example.marketplace.repository.HouseHelpRepository;
import com.example.marketplace.repository.RoleRepository;
import com.example.marketplace.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HouseHelpRepository houseHelpRepository;
    private final HomeOwnerRepository homeOwnerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO registerUser(RegisterRequest dto, String roleName) {

        // Check duplicates
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Default role = HOMEOWNER
        String resolvedRoleName = (roleName == null || roleName.trim().isEmpty())
                ? "HOMEOWNER"
                : roleName.trim().toUpperCase();

        // Load role
        Role role = roleRepository.findByName(resolvedRoleName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Role " + resolvedRoleName + " does not exist. Please create it first."
                ));

        // Create user
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setRoles(new HashSet<>(Collections.singletonList(role)));

        User savedUser = userRepository.save(user);

        // Create default records for role
        switch (resolvedRoleName) {
            case "HOUSEHELP" -> {
                HouseHelp houseHelp = new HouseHelp();
                houseHelp.setUser(savedUser);
                houseHelp.setVerified(false);
                houseHelpRepository.save(houseHelp);
            }
            case "HOMEOWNER" -> {
                HomeOwner homeOwner = new HomeOwner();
                homeOwner.setUser(savedUser);
                homeOwnerRepository.save(homeOwner);
            }
            default -> {
            }
        }

        return toUserResponseDTO(savedUser);
    }

    public UserResponseDTO addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole = roleRepository.findByName(roleName.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Role> existingRoles = user.getRoles();

        boolean isHouseHelp = existingRoles.stream().anyMatch(r -> r.getName().equalsIgnoreCase("HOUSEHELP"));
        boolean isHomeOwner = existingRoles.stream().anyMatch(r -> r.getName().equalsIgnoreCase("HOMEOWNER"));

        if (newRole.getName().equalsIgnoreCase("HOUSEHELP")) {

            existingRoles.removeIf(r -> r.getName().equalsIgnoreCase("HOMEOWNER"));

            if (user.getHouseHelp() == null) {
                HouseHelp houseHelp = new HouseHelp();
                houseHelp.setUser(user);
                houseHelp.setVerified(false);
                houseHelpRepository.save(houseHelp);
            }

        } else if (newRole.getName().equalsIgnoreCase("HOMEOWNER")) {

            existingRoles.removeIf(r -> r.getName().equalsIgnoreCase("HOUSEHELP"));

            if (user.getHouseHelp() != null) {
                houseHelpRepository.delete(user.getHouseHelp());
                user.setHouseHelp(null);
            }

            if (user.getHomeOwner() == null) {
                HomeOwner homeOwner = new HomeOwner();
                homeOwner.setUser(user);
                homeOwner.setNumberOfDependents(0);
                homeOwner.setHouseType(null);
                homeOwner.setNumberOfRooms(null);
                homeOwner.setHomeLocation(null);
                homeOwnerRepository.save(homeOwner);
            }
        }

        existingRoles.add(newRole);

        user.setRoles(existingRoles);
        User updatedUser = userRepository.save(user);

        return toUserResponseDTO(updatedUser);
    }

    @Transactional
    public UserResponseDTO updateUserRoles(Long userId, List<String> roles) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> requestedRoles = roles.stream()
                .map(r -> roleRepository.findByName("ROLE_" + r.toUpperCase())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + r)))
                .collect(Collectors.toSet());

        boolean wantsHouseHelp = hasRole(requestedRoles, "HOUSEHELP");
        boolean wantsHomeOwner = hasRole(requestedRoles, "HOMEOWNER");


        if (wantsHouseHelp && wantsHomeOwner) {
            throw new RuntimeException("User cannot be HOUSEHELP and HOMEOWNER at the same time");
        }

        // =========================
        // HOMEOWNER → HOUSEHELP
        // =========================
        if (wantsHouseHelp) {

            // deactivate homeowner
            if (user.getHomeOwner() != null && user.getHomeOwner().isActive()) {
                user.getHomeOwner().setActive(false);
            }

            // activate or create househelp
            if (user.getHouseHelp() == null) {
                HouseHelp houseHelp = new HouseHelp();
                houseHelp.setUser(user);
                houseHelp.setVerified(false);
                houseHelp.setActive(true);
                houseHelpRepository.save(houseHelp);
            } else {
                user.getHouseHelp().setActive(true);
            }
        }

        // =========================
        // HOUSEHELP → HOMEOWNER
        // =========================
        if (wantsHomeOwner) {

            // deactivate househelp
            if (user.getHouseHelp() != null && user.getHouseHelp().isActive()) {
                user.getHouseHelp().setActive(false);
            }

            // activate or create homeowner
            if (user.getHomeOwner() == null) {
                HomeOwner homeOwner = new HomeOwner();
                homeOwner.setUser(user);
                homeOwner.setActive(true);
                homeOwnerRepository.save(homeOwner);
            } else {
                user.getHomeOwner().setActive(true);
            }
        }

        user.setRoles(requestedRoles);
        User savedUser = userRepository.save(user);

        return mapToUserResponseDTO(savedUser);
    }

    private boolean hasRole(Set<Role> roles, String role) {
        return roles.stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(role));
    }

    private UserResponseDTO toUserResponseDTO(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setPhoneNumber(user.getPhoneNumber());
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
                    dto.setPhoneNumber(user.getPhoneNumber());
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
        dto.setPhoneNumber(user.getPhoneNumber());
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

    private HouseHelpResponseDTO mapToHouseHelpDTO(HouseHelp houseHelp) {
        if (houseHelp == null) return null;

        HouseHelpResponseDTO dto = new HouseHelpResponseDTO();
        dto.setId(houseHelp.getId());
        dto.setNumberOfChildren(houseHelp.getNumberOfChildren());
        dto.setLanguages(houseHelp.getLanguages());
        dto.setLevelOfEducation(houseHelp.getLevelOfEducation());
        dto.setContactPersons(houseHelp.getContactPersons());
        dto.setHomeLocation(houseHelp.getHomeLocation());
        dto.setCurrentLocation(houseHelp.getCurrentLocation());
        dto.setNationalId(houseHelp.getNationalId());
        dto.setMedicalReport(houseHelp.getMedicalReport());
        dto.setGoodConduct(houseHelp.getGoodConduct());
        dto.setYearsOfExperience(houseHelp.getYearsOfExperience());
        dto.setReligion(houseHelp.getReligion());
        dto.setSkills(houseHelp.getSkills());

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

    public Page<User> findByFilterAndPage(UserFilterDTO filter, Pageable pageable) {
        return userRepository.findAll(buildSpecification(filter), pageable);
    }

    public UserResponseDTO updateUser(Long userId, UpdateUserRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // --- Update email with uniqueness check
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(dto.getEmail());
        }

        // --- Update phone with uniqueness check
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
                throw new IllegalArgumentException("Phone number already exists");
            }
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        // --- Update name
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        // --- Handle role change
        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            String roleName = dto.getRole().trim().toUpperCase();
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role " + roleName + " does not exist"));
            user.setRoles(Set.of(role));

            switch (roleName) {
                case "HOMEOWNER" -> {
                    // Deactivate old HouseHelp if exists
                    houseHelpRepository.findByUserAndActiveTrue(user).ifPresent(hh -> {
                        hh.setActive(false);
                        houseHelpRepository.save(hh);
                    });

                    // Create or reactivate HomeOwner
                    HomeOwner homeOwner = homeOwnerRepository.findByUser(user)
                            .orElse(new HomeOwner());
                    homeOwner.setUser(user);
                    homeOwner.setActive(true);
                    homeOwner.setNumberOfDependents(dto.getNumberOfDependents());
                    homeOwner.setHouseType(dto.getHouseType());
                    homeOwner.setNumberOfRooms(dto.getNumberOfRooms());
                    homeOwner.setHomeLocation(dto.getHomeLocation());
                    homeOwnerRepository.save(homeOwner);
                }
                case "HOUSEHELP" -> {
                    // Deactivate old HomeOwner if exists
                    homeOwnerRepository.findByUserAndActiveTrue(user).ifPresent(ho -> {
                        ho.setActive(false);
                        homeOwnerRepository.save(ho);
                    });

                    // Create or reactivate HouseHelp
                    HouseHelp hh = houseHelpRepository.findByUser(user)
                            .orElse(new HouseHelp());
                    hh.setUser(user);
                    hh.setActive(true);
                    hh.setVerified(dto.getVerified() != null ? dto.getVerified() : false);
                    hh.setYearsOfExperience(dto.getYearsOfExperience());
                    if (dto.getSkills() != null) {
                        hh.setSkills(Arrays.stream(dto.getSkills().split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList()));
                    }
                    houseHelpRepository.save(hh);
                }
            }
        }

        User savedUser = userRepository.save(user);
        return toUserResponseDTO(savedUser);
    }


    private Specification<User> buildSpecification(UserFilterDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getName().toLowerCase() + "%"));
            }

            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getName().toLowerCase() + "%"));
            }

            if (filter.getRoles() != null) {
                predicates.add(cb.equal(root.get("roles"), filter.getRoles()));
            }

            if (filter.getId() != null) {
                predicates.add(cb.equal(root.get("id"), filter.getId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}