package com.example.marketplace.service;

import com.example.marketplace.dto.*;
import com.example.marketplace.mapper.UserMapper;
import com.example.marketplace.model.*;
import com.example.marketplace.repository.*;
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
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public UserResponseDTO registerUser(RegisterRequest dto, String roleName) {

        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        String email = dto.getEmail();
        if (email != null && email.isBlank()) {
            email = null;
        }

        if (userRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        if (email != null && userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        String resolvedRoleName = (roleName == null || roleName.trim().isEmpty())
                ? "HOMEOWNER"
                : roleName.trim().toUpperCase();

        Role role = roleRepository.findByName(resolvedRoleName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Role " + resolvedRoleName + " does not exist. Please create it first."
                ));

        User user = new User();
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setRoles(Set.of(role));

        User savedUser = userRepository.save(user);

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
            case "AGENT" -> {
                Agent agent = new Agent();
                agent.setUser(savedUser);
                agent.setFullName(savedUser.getName());
                agent.setEmail(savedUser.getEmail());
                agent.setPhoneNumber(savedUser.getPhoneNumber());
                agent.setVerified(false);
                agentRepository.save(agent);
            }
        }

        return userMapper.toDTO(savedUser);
    }

    public UserResponseDTO addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole = roleRepository.findByName(roleName.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Role> existingRoles = user.getRoles();

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

        } else if (newRole.getName().equalsIgnoreCase("AGENT")) {
            // Agent can coexist with other roles — create only if not exists
            if (agentRepository.findByUser(user).isEmpty()) {
                Agent agent = new Agent();
                agent.setUser(user);
                agent.setFullName(user.getName());
                agent.setEmail(user.getEmail());
                agent.setPhoneNumber(user.getPhoneNumber());
                agent.setVerified(false);
                agentRepository.save(agent);
            }
        }

        existingRoles.add(newRole);
        user.setRoles(existingRoles);
        User updatedUser = userRepository.save(user);

        return userMapper.toDTO(updatedUser);
    }

    @Transactional
    public UserResponseDTO updateUserRoles(Long userId, List<String> roles) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> requestedRoles = roles.stream()
                .map(r -> roleRepository.findByName(r.toUpperCase())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + r)))
                .collect(Collectors.toSet());

        boolean wantsHouseHelp = hasRole(requestedRoles, "HOUSEHELP");
        boolean wantsHomeOwner = hasRole(requestedRoles, "HOMEOWNER");
        boolean wantsAgent     = hasRole(requestedRoles, "AGENT");

        if (wantsHouseHelp && wantsHomeOwner) {
            throw new RuntimeException("User cannot be HOUSEHELP and HOMEOWNER at the same time");
        }

        // ── HOMEOWNER → HOUSEHELP ──────────────────────────
        if (wantsHouseHelp) {
            if (user.getHomeOwner() != null && user.getHomeOwner().isActive()) {
                user.getHomeOwner().setActive(false);
            }
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

        // ── HOUSEHELP → HOMEOWNER ──────────────────────────
        if (wantsHomeOwner) {
            if (user.getHouseHelp() != null && user.getHouseHelp().isActive()) {
                user.getHouseHelp().setActive(false);
            }
            if (user.getHomeOwner() == null) {
                HomeOwner homeOwner = new HomeOwner();
                homeOwner.setUser(user);
                homeOwner.setActive(true);
                homeOwnerRepository.save(homeOwner);
            } else {
                user.getHomeOwner().setActive(true);
            }
        }

        // ── AGENT ──────────────────────────────────────────
        if (wantsAgent) {
            if (agentRepository.findByUser(user).isEmpty()) {
                Agent agent = new Agent();
                agent.setUser(user);
                agent.setFullName(user.getName());
                agent.setEmail(user.getEmail());
                agent.setPhoneNumber(user.getPhoneNumber());
                agent.setVerified(false);
                agentRepository.save(agent);
            }
        }

        user.setRoles(requestedRoles);
        User savedUser = userRepository.save(user);

        return mapToUserResponseDTO(savedUser);
    }

    private boolean hasRole(Set<Role> roles, String role) {
        return roles.stream().anyMatch(r -> r.getName().equalsIgnoreCase(role));
    }

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    public Page<UserResponseDTO> findByFilterAndPage(UserFilterDTO filter, Pageable pageable) {
        return userRepository.findAll(buildSpecification(filter), pageable)
                .map(userMapper::toDTO);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByEmailOrPhone(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhoneNumber(identifier))
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
        UserResponseDTO dto = userMapper.toDTO(user);
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
        dto.setHomeCounty(houseHelp.getHomeCounty());
        dto.setCurrentLocation(houseHelp.getCurrentLocation());
        dto.setCurrentCounty(houseHelp.getCurrentCounty());
        dto.setNationalId(houseHelp.getNationalId());
        dto.setNationalIdDocument(houseHelp.getNationalIdDocument());
        dto.setProfilePictureDocument(houseHelp.getProfilePictureDocument());
        dto.setMedicalReport(houseHelp.getMedicalReport());
        dto.setGoodConduct(houseHelp.getGoodConduct());
        dto.setYearsOfExperience(houseHelp.getYearsOfExperience());
        dto.setReligion(houseHelp.getReligion());
        dto.setSkills(houseHelp.getSkills());
        dto.setHeight(houseHelp.getHeight());
        dto.setWeight(houseHelp.getWeight());
        dto.setAge(houseHelp.getAge());
        dto.setHouseHelpType(houseHelp.getHouseHelpType());
        dto.setGender(houseHelp.getGender());
        dto.setContactPersonsPhoneNumber(houseHelp.getContactPersonsPhoneNumber());
        dto.setMaxTravelDistanceKm(houseHelp.getMaxTravelDistanceKm());

        if (houseHelp.getPinLocation() != null) {
            GeoLocationResponseDTO loc = new GeoLocationResponseDTO();
            loc.setLatitude(houseHelp.getPinLocation().getLatitude());
            loc.setLongitude(houseHelp.getPinLocation().getLongitude());
            loc.setPlaceName(houseHelp.getPinLocation().getPlaceName());
            loc.setAddressLine(houseHelp.getPinLocation().getAddressLine());
            dto.setPinLocation(loc);
        }

        if (houseHelp.getPreferences() != null) {
            HouseHelpPreferenceResponseDTO p = new HouseHelpPreferenceResponseDTO();
            p.setHouseHelpType(houseHelp.getPreferences().getHouseHelpType());
            p.setMinExperience(houseHelp.getPreferences().getMinExperience());
            p.setPreferredLocation(houseHelp.getPreferences().getPreferredLocation());
            p.setPreferredSkills(houseHelp.getPreferences().getPreferredSkills());
            p.setPreferredLanguages(houseHelp.getPreferences().getPreferredLanguages());
            p.setPreferredChildAgeRanges(houseHelp.getPreferences().getPreferredChildAgeRanges());
            p.setPreferredMaxChildren(houseHelp.getPreferences().getPreferredMaxChildren());
            p.setPreferredServices(houseHelp.getPreferences().getPreferredServices());
            p.setPreferredReligion(houseHelp.getPreferences().getPreferredReligion());
            p.setOkayWithPets(houseHelp.getPreferences().getOkayWithPets());
            p.setMinSalary(houseHelp.getPreferences().getMinSalary());
            p.setMaxSalary(houseHelp.getPreferences().getMaxSalary());
            dto.setPreferences(p);
        }

        return dto;
    }

    private HomeOwnerUpdateDTO mapToHomeOwnerDTO(HomeOwner homeOwner) {
        if (homeOwner == null) return null;

        HomeOwnerUpdateDTO dto = new HomeOwnerUpdateDTO();
        dto.setId(homeOwner.getId());
        dto.setNationalId(homeOwner.getNationalId());
        dto.setHomeLocation(homeOwner.getHomeLocation());
        dto.setPhoneNumber(homeOwner.getPhoneNumber());
        dto.setNationalIdDocument(homeOwner.getNationalIdDocument());
        dto.setProfilePictureDocument(homeOwner.getProfilePictureDocument());
        return dto;
    }

    public UserResponseDTO updateUser(Long userId, UpdateUserRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(dto.getEmail());
        }

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
                throw new IllegalArgumentException("Phone number already exists");
            }
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            String roleName = dto.getRole().trim().toUpperCase();
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role " + roleName + " does not exist"));
            user.setRoles(Set.of(role));

            switch (roleName) {
                case "HOMEOWNER" -> {
                    houseHelpRepository.findByUserAndActiveTrue(user).ifPresent(hh -> {
                        hh.setActive(false);
                        houseHelpRepository.save(hh);
                    });
                    HomeOwner homeOwner = homeOwnerRepository.findByUser(user)
                            .orElse(new HomeOwner());
                    homeOwner.setUser(user);
                    homeOwner.setActive(true);
                    homeOwner.setNumberOfDependents(dto.getNumberOfDependents());
                    homeOwner.setHouseType(dto.getHouseType());
                    homeOwner.setNumberOfRooms(dto.getNumberOfRooms());
                    homeOwner.setHomeLocation(dto.getHomeLocation());
                    user.setHomeOwner(homeOwner);
                    homeOwnerRepository.save(homeOwner);
                }
                case "HOUSEHELP" -> {
                    homeOwnerRepository.findByUserAndActiveTrue(user).ifPresent(ho -> {
                        ho.setActive(false);
                        homeOwnerRepository.save(ho);
                    });
                    HouseHelp houseHelp = houseHelpRepository.findByUser(user)
                            .orElse(new HouseHelp());
                    houseHelp.setUser(user);
                    houseHelp.setActive(true);
                    houseHelp.setVerified(dto.getVerified() != null ? dto.getVerified() : false);
                    houseHelp.setYearsOfExperience(dto.getYearsOfExperience());
                    houseHelp.setSkills(dto.getSkills());
                    user.setHouseHelp(houseHelp);
                    houseHelpRepository.save(houseHelp);
                }
                case "AGENT" -> {
                    Agent agent = agentRepository.findByUser(user)
                            .orElse(new Agent());
                    agent.setUser(user);
                    agent.setFullName(user.getName());
                    agent.setEmail(user.getEmail());
                    agent.setPhoneNumber(user.getPhoneNumber());
                    agent.setVerified(false);
                    agentRepository.save(agent);
                }
            }
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    private Specification<User> buildSpecification(UserFilterDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getEmail().toLowerCase() + "%"));
            }

            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
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
