package com.example.marketplace.service;

import com.example.marketplace.dto.HouseHelpFilterDTO;
import com.example.marketplace.dto.HouseHelpUpdateDTO;
import com.example.marketplace.dto.HouseHelpUpdateResponseDTO;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.repository.HouseHelpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseHelpService {
    private final HouseHelpRepository houseHelpRepository;

    public Page<HouseHelp> getAllHouseHelps(Pageable pageable) {
        return houseHelpRepository.findAll(pageable);
    }

    public HouseHelpUpdateResponseDTO updateHouseHelp(Long houseHelpId, HouseHelpUpdateDTO dto) {
        HouseHelp houseHelp = houseHelpRepository.findById(houseHelpId)
                .orElseThrow(() -> new RuntimeException("HouseHelp not found"));

        if (dto.getNumberOfChildren() != null) houseHelp.setNumberOfChildren(dto.getNumberOfChildren());
        if (dto.getLanguages() != null) houseHelp.setLanguages(dto.getLanguages());
        if (dto.getLevelOfEducation() != null) houseHelp.setLevelOfEducation(dto.getLevelOfEducation());
        if (dto.getContactPersons() != null) houseHelp.setContactPersons(dto.getContactPersons());
        if (dto.getHomeLocation() != null) houseHelp.setHomeLocation(dto.getHomeLocation());
        if (dto.getCurrentLocation() != null) houseHelp.setCurrentLocation(dto.getCurrentLocation());
        if (dto.getNationalId() != null) houseHelp.setNationalId(dto.getNationalId());
        if (dto.getMedicalReport() != null) houseHelp.setMedicalReport(dto.getMedicalReport());
        if (dto.getGoodConduct() != null) houseHelp.setGoodConduct(dto.getGoodConduct());
        if (dto.getYearsOfExperience() != null) houseHelp.setYearsOfExperience(dto.getYearsOfExperience());
        if (dto.getReligion() != null) houseHelp.setReligion(dto.getReligion());
        if (dto.getSkills() != null) houseHelp.setSkills(dto.getSkills());

        HouseHelp updated = houseHelpRepository.save(houseHelp);

        List<String> missingFields = getMissingFields(updated);

        return new HouseHelpUpdateResponseDTO(updated, missingFields);
    }

    public HouseHelp verifyHouseHelp(Long houseHelpId) {
        HouseHelp houseHelp = houseHelpRepository.findById(houseHelpId)
                .orElseThrow(() -> new RuntimeException("HouseHelp not found"));

        List<String> missingFields = getMissingFields(houseHelp);

        if (!missingFields.isEmpty()) {
            throw new RuntimeException("Profile incomplete. Missing fields: " + String.join(", ", missingFields));
        }

        houseHelp.setVerified(true);
        return houseHelpRepository.save(houseHelp);
    }

    private List<String> getMissingFields(HouseHelp houseHelp) {
        List<String> missing = new ArrayList<>();

        if (houseHelp.getNumberOfChildren() == null) missing.add("numberOfChildren");
        if (houseHelp.getLanguages() == null || houseHelp.getLanguages().isEmpty()) missing.add("languages");
        if (isBlank(houseHelp.getLevelOfEducation())) missing.add("levelOfEducation");
        if (isBlank(houseHelp.getContactPersons())) missing.add("contactPersons");
        if (isBlank(houseHelp.getHomeLocation())) missing.add("homeLocation");
        if (isBlank(houseHelp.getCurrentLocation())) missing.add("currentLocation");
        if (isBlank(houseHelp.getNationalId())) missing.add("nationalId");
        if (isBlank(houseHelp.getMedicalReport())) missing.add("medicalReport");
        if (isBlank(houseHelp.getGoodConduct())) missing.add("goodConduct");
        if (houseHelp.getYearsOfExperience() == null) missing.add("yearsOfExperience");
        if (isBlank(houseHelp.getReligion())) missing.add("religion");
        if (houseHelp.getSkills() == null || houseHelp.getSkills().isEmpty()) missing.add("skills");

        return missing;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public List<HouseHelp> findByFilter(HouseHelpFilterDTO filter) {
        return houseHelpRepository.findByFilter(filter);
    }
}
