package com.example.marketplace.service;

import com.example.marketplace.dto.HomeOwnerFilterDTO;
import com.example.marketplace.dto.HomeOwnerUpdateDTO;
import com.example.marketplace.dto.HomeOwnerUpdateResponseDTO;
import com.example.marketplace.model.HomeOwner;

import com.example.marketplace.repository.HomeOwnerRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeOwnerService {
    private final HomeOwnerRepository homeOwnerRepository;

    public Page<HomeOwner> getAllHomeOwners(Pageable pageable) {
        return homeOwnerRepository.findAll(pageable);
    }

    public HomeOwnerUpdateResponseDTO updateHomeOwner(Long id, HomeOwnerUpdateDTO dto) {
        HomeOwner homeOwner = homeOwnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

        if (dto.getFullName() != null) homeOwner.setFullName(dto.getFullName());
        if (dto.getNationalId() != null) homeOwner.setNationalId(dto.getNationalId());
        if (dto.getPhoneNumber() != null) homeOwner.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getEmail() != null) homeOwner.setEmail(dto.getEmail());
        if (dto.getHomeLocation() != null) homeOwner.setHomeLocation(dto.getHomeLocation());
        if (dto.getHouseType() != null) homeOwner.setHouseType(dto.getHouseType());
        if (dto.getNumberOfDependents() != null) homeOwner.setNumberOfDependents(dto.getNumberOfDependents());
        if (dto.getIdDocument() != null) homeOwner.setNationalIdDocument(dto.getIdDocument());
        if (dto.getNumberOfRooms() != null) homeOwner.setNumberOfRooms(dto.getNumberOfRooms());

        HomeOwner updated = homeOwnerRepository.save(homeOwner);

        List<String> missingFields = getMissingFields(updated);

        return new HomeOwnerUpdateResponseDTO(updated, missingFields);
    }

    public HomeOwner verifyHomeOwner(Long id) {
        HomeOwner homeOwner = homeOwnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

        List<String> missingFields = getMissingFields(homeOwner);

        if (!missingFields.isEmpty()) {
            throw new RuntimeException("Profile incomplete. Missing fields: " + String.join(", ", missingFields));
        }

        homeOwner.setVerified(true);
        return homeOwnerRepository.save(homeOwner);
    }

    List<String> getMissingFields(HomeOwner homeOwner) {
        List<String> missing = new ArrayList<>();

        if (isBlank(homeOwner.getFullName())) missing.add("fullName");
        if (isBlank(homeOwner.getNationalId())) missing.add("nationalId");
        if (isBlank(homeOwner.getPhoneNumber())) missing.add("phoneNumber");
        if (isBlank(homeOwner.getEmail())) missing.add("email");
        if (isBlank(homeOwner.getHomeLocation())) missing.add("homeLocation");
        if (isBlank(homeOwner.getHouseType())) missing.add("houseType");
        if (homeOwner.getNumberOfDependents() == null) missing.add("numberOfDependents");
        if (isBlank(homeOwner.getNationalIdDocument())) missing.add("idDocument");
        if (isBlank(homeOwner.getNumberOfRooms())) missing.add("numberOfRooms");

        return missing;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public HomeOwner getHomeOwnerById(Long id) {
        return homeOwnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Homeowner not found with id: " + id));
    }

    public HomeOwner setSecurityCleared(Long id, boolean securityCleared) {
        HomeOwner homeOwner = homeOwnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

        homeOwner.setSecurityCleared(securityCleared);
        return homeOwnerRepository.save(homeOwner);
    }

    public HomeOwner setSecurityCleared(
            Long id,
            boolean securityCleared,
            String comments
    ) {
        HomeOwner homeOwner = homeOwnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

        homeOwner.setSecurityCleared(securityCleared);
        homeOwner.setSecurityClearanceComments(comments);

        return homeOwnerRepository.save(homeOwner);
    }

    public HomeOwner setActiveStatus(Long id, boolean active) {
        HomeOwner homeOwner = homeOwnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

        homeOwner.setActive(active);
        return homeOwnerRepository.save(homeOwner);
    }

    public Page<HomeOwner> findByFilterAndPage(HomeOwnerFilterDTO filter, Pageable pageable) {
        return homeOwnerRepository.findAll(buildSpecification(filter), pageable);
    }

    private Specification<HomeOwner> buildSpecification(HomeOwnerFilterDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getHomeLocation() != null && !filter.getHomeLocation().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("homeLocation")), "%" + filter.getHomeLocation().toLowerCase() + "%"));
            }

            if (filter.getHouseType() != null) {
                predicates.add(cb.equal(root.get("houseType"), filter.getHouseType()));
            }

            if (filter.getNumberOfRooms() != null) {
                predicates.add(cb.equal(root.get("numberOfRooms"), filter.getNumberOfRooms()));
            }

            if (filter.getActive() != null) {
                predicates.add(cb.equal(root.get("active"), filter.getActive()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
