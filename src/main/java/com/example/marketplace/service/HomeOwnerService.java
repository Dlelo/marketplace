package com.example.marketplace.service;

import com.example.marketplace.dto.*;
import com.example.marketplace.model.GeoLocation;
import com.example.marketplace.model.HomeOwner;
import com.example.marketplace.model.HomeOwnerPreference;
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

    // -------------------- CRUD & Updates --------------------

    public Page<HomeOwner> getAllHomeOwners(Pageable pageable) {
        return homeOwnerRepository.findAll(pageable);
    }

    public HomeOwnerUpdateResponseDTO updateHomeOwner(Long id, HomeOwnerUpdateDTO dto) {

        HomeOwner homeOwner = getHomeOwnerOrThrow(id);

        updateBasicDetails(homeOwner, dto);
        updatePreferences(homeOwner, dto.getPreferences());
        updatePinLocation(homeOwner, dto.getPinLocation());

        HomeOwner saved = homeOwnerRepository.save(homeOwner);

        return mapToUpdateResponse(saved, getMissingFields(saved));
    }

    public HomeOwner verifyHomeOwner(Long id) {
        HomeOwner homeOwner = getHomeOwnerOrThrow(id);

        List<String> missingFields = getMissingFields(homeOwner);
        if (!missingFields.isEmpty()) {
            throw new RuntimeException("Profile incomplete. Missing fields: " + String.join(", ", missingFields));
        }

        homeOwner.setVerified(true);
        return homeOwnerRepository.save(homeOwner);
    }

    public HomeOwner setActiveStatus(Long id, boolean active) {
        HomeOwner homeOwner = getHomeOwnerOrThrow(id);
        homeOwner.setActive(active);
        return homeOwnerRepository.save(homeOwner);
    }

    public HomeOwner setSecurityCleared(Long id, boolean securityCleared, String comments) {
        HomeOwner homeOwner = getHomeOwnerOrThrow(id);
        homeOwner.setSecurityCleared(securityCleared);
        homeOwner.setSecurityClearanceComments(comments);
        return homeOwnerRepository.save(homeOwner);
    }

    public HomeOwner getHomeOwnerById(Long id) {
        return getHomeOwnerOrThrow(id);
    }

    // -------------------- Helper: Fetch --------------------

    private HomeOwner getHomeOwnerOrThrow(Long id) {
        return homeOwnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found with id: " + id));
    }

    // -------------------- Update Helpers --------------------

    private void updateBasicDetails(HomeOwner homeOwner, HomeOwnerUpdateDTO dto) {
        if (dto.getFullName() != null) homeOwner.setFullName(dto.getFullName());
        if (dto.getNationalId() != null) homeOwner.setNationalId(dto.getNationalId());
        if (dto.getPhoneNumber() != null) homeOwner.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getEmail() != null) homeOwner.setEmail(dto.getEmail());
        if (dto.getHomeLocation() != null) homeOwner.setHomeLocation(dto.getHomeLocation());
        if (dto.getHouseType() != null) homeOwner.setHouseType(dto.getHouseType());
        if (dto.getNumberOfDependents() != null) homeOwner.setNumberOfDependents(dto.getNumberOfDependents());
        if (dto.getIdDocument() != null) homeOwner.setNationalIdDocument(dto.getIdDocument());
        if (dto.getNumberOfRooms() != null) homeOwner.setNumberOfRooms(dto.getNumberOfRooms());
        if (dto.getMaxDistanceKm() != null) homeOwner.setMaxDistanceKm(dto.getMaxDistanceKm());
    }

    private void updatePreferences(HomeOwner homeOwner, HomeOwnerPreferenceUpdateDTO dto) {
        if (dto == null) return;

        if (homeOwner.getPreferences() == null) {
            homeOwner.setPreferences(new HomeOwnerPreference());
        }

        HomeOwnerPreference pref = homeOwner.getPreferences();

        if (dto.getHouseHelpType() != null) pref.setHouseHelpType(dto.getHouseHelpType());
        if (dto.getMinExperience() != null) pref.setMinExperience(dto.getMinExperience());
        if (dto.getLocation() != null) pref.setLocation(dto.getLocation());
        if (dto.getPreferredSkills() != null) pref.setPreferredSkills(dto.getPreferredSkills());
        if (dto.getPreferredLanguages() != null) pref.setPreferredLanguages(dto.getPreferredLanguages());
        if (dto.getMinMatchScore() != null) pref.setMinMatchScore(dto.getMinMatchScore());
        if (dto.getChildrenAgeRanges() != null) pref.setChildrenAgeRanges(dto.getChildrenAgeRanges());
        if (dto.getNumberOfChildren() != null) pref.setNumberOfChildren(dto.getNumberOfChildren());
        if (dto.getRequiredServices() != null) pref.setRequiredServices(dto.getRequiredServices());
        if (dto.getHasPets() != null) pref.setHasPets(dto.getHasPets());
        if (dto.getReligionPreference() != null) pref.setReligionPreference(dto.getReligionPreference());
        if (dto.getRequiresSecurityCleared() != null) pref.setRequiresSecurityCleared(dto.getRequiresSecurityCleared());
        if (dto.getPreferredMinAge() != null) pref.setPreferredMinAge(dto.getPreferredMinAge());
        if (dto.getPreferredMaxAge() != null) pref.setPreferredMaxAge(dto.getPreferredMaxAge());
        if (dto.getMinSalary() != null) pref.setMinSalary(dto.getMinSalary());
        if (dto.getMaxSalary() != null) pref.setMaxSalary(dto.getMaxSalary());
        if (dto.getPreferredMinSalary() != null) pref.setPreferredMinSalary(dto.getPreferredMinSalary());
        if (dto.getPreferredMaxSalary() != null) pref.setPreferredMaxSalary(dto.getPreferredMaxSalary());
    }

    private void updatePinLocation(HomeOwner homeOwner, GeoLocationUpdateDTO dto) {
        if (dto == null) return;

        if (homeOwner.getPinLocation() == null) {
            homeOwner.setPinLocation(new GeoLocation());
        }

        GeoLocation location = homeOwner.getPinLocation();

        if (dto.getLatitude() != null) location.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) location.setLongitude(dto.getLongitude());
        if (dto.getPlaceName() != null) location.setPlaceName(dto.getPlaceName());
        if (dto.getAddressLine() != null) location.setAddressLine(dto.getAddressLine());
    }

    // -------------------- Mapping --------------------

    private HomeOwnerUpdateResponseDTO mapToUpdateResponse(HomeOwner h, List<String> missingFields) {

        GeoLocationResponseDTO location = null;
        if (h.getPinLocation() != null) {
            location = new GeoLocationResponseDTO();
            location.setLatitude(h.getPinLocation().getLatitude());
            location.setLongitude(h.getPinLocation().getLongitude());
            location.setPlaceName(h.getPinLocation().getPlaceName());
            location.setAddressLine(h.getPinLocation().getAddressLine());
        }

        HomeOwnerPreferenceResponseDTO pref = null;
        if (h.getPreferences() != null) {
            pref = new HomeOwnerPreferenceResponseDTO();
            pref.setHouseHelpType(h.getPreferences().getHouseHelpType());
            pref.setMinExperience(h.getPreferences().getMinExperience());
            pref.setLocation(h.getPreferences().getLocation());
            pref.setPreferredSkills(h.getPreferences().getPreferredSkills());
            pref.setPreferredLanguages(h.getPreferences().getPreferredLanguages());
            pref.setMinMatchScore(h.getPreferences().getMinMatchScore());
            pref.setChildrenAgeRanges(h.getPreferences().getChildrenAgeRanges());
            pref.setNumberOfChildren(h.getPreferences().getNumberOfChildren());
            pref.setRequiredServices(h.getPreferences().getRequiredServices());
            pref.setHasPets(h.getPreferences().getHasPets());
            pref.setReligionPreference(h.getPreferences().getReligionPreference());
            pref.setRequiresSecurityCleared(h.getPreferences().getRequiresSecurityCleared());
            pref.setPreferredMinAge(h.getPreferences().getPreferredMinAge());
            pref.setPreferredMaxAge(h.getPreferences().getPreferredMaxAge());
            pref.setPreferredMinSalary(h.getPreferences().getPreferredMinSalary());
            pref.setPreferredMaxSalary(h.getPreferences().getPreferredMaxSalary());
        }

        return new HomeOwnerUpdateResponseDTO(
                h.getId(),
                h.getFullName(),
                h.getPhoneNumber(),
                h.getEmail(),
                h.getHomeLocation(),
                h.getHouseType(),
                h.getNumberOfRooms(),
                h.getNumberOfDependents(),
                location,
                h.getMaxDistanceKm(),
                pref,
                missingFields
        );
    }

    // -------------------- Validation --------------------

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

    // -------------------- Filtering --------------------

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
