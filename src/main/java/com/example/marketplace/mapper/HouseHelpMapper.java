package com.example.marketplace.mapper;

import com.example.marketplace.dto.GeoLocationResponseDTO;
import com.example.marketplace.dto.HouseHelpPreferenceResponseDTO;
import com.example.marketplace.dto.HouseHelpResponseDTO;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.User;
import org.springframework.stereotype.Component;

@Component
public class HouseHelpMapper {

    public HouseHelpResponseDTO toDTO(HouseHelp houseHelp) {
        if (houseHelp == null) return null;

        HouseHelpResponseDTO dto = new HouseHelpResponseDTO();

        dto.setId(houseHelp.getId());
        dto.setVerified(houseHelp.isVerified());
        dto.setActive(houseHelp.isActive());
        dto.setSecurityCleared(houseHelp.isSecurityCleared());
        dto.setSecurityClearanceComments(houseHelp.getSecurityClearanceComments());
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
        dto.setContactPersonsPhoneNumber(houseHelp.getContactPersonsPhoneNumber());
        dto.setMedicalReport(houseHelp.getMedicalReport());
        dto.setGoodConduct(houseHelp.getGoodConduct());
        dto.setYearsOfExperience(houseHelp.getYearsOfExperience());
        dto.setReligion(houseHelp.getReligion());
        dto.setHeight(houseHelp.getHeight());
        dto.setWeight(houseHelp.getWeight());
        dto.setAge(houseHelp.getAge());
        dto.setGender(houseHelp.getGender());
        dto.setLocalAuthorityVerificationDocument(houseHelp.getLocalAuthorityVerificationDocument());
        dto.setHouseHelpType(houseHelp.getHouseHelpType());
        dto.setAvailability(houseHelp.getAvailability());
        dto.setExperienceSummary(houseHelp.getExperienceSummary());
        dto.setChildAgeRanges(houseHelp.getChildAgeRanges());
        dto.setServices(houseHelp.getServices());
        dto.setMaxChildren(houseHelp.getMaxChildren());
        dto.setSkills(houseHelp.getSkills());
        dto.setMaxTravelDistanceKm(houseHelp.getMaxTravelDistanceKm());
        dto.setHiringStatus(houseHelp.getHiringStatus());

        // Map user summary
        if (houseHelp.getUser() != null) {
            User user = houseHelp.getUser();
            HouseHelpResponseDTO.UserSummaryDTO userDTO = new HouseHelpResponseDTO.UserSummaryDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            dto.setUser(userDTO);
        }

        // Map pin location
        if (houseHelp.getPinLocation() != null) {
            GeoLocationResponseDTO loc = new GeoLocationResponseDTO();
            loc.setLatitude(houseHelp.getPinLocation().getLatitude());
            loc.setLongitude(houseHelp.getPinLocation().getLongitude());
            loc.setPlaceName(houseHelp.getPinLocation().getPlaceName());
            loc.setAddressLine(houseHelp.getPinLocation().getAddressLine());
            dto.setPinLocation(loc);
        }

        // Map preferences
        if (houseHelp.getPreferences() != null) {
            HouseHelpPreferenceResponseDTO preference = new HouseHelpPreferenceResponseDTO();
            preference.setHouseHelpType(houseHelp.getPreferences().getHouseHelpType());
            preference.setMinExperience(houseHelp.getPreferences().getMinExperience());
            preference.setPreferredLocation(houseHelp.getPreferences().getPreferredLocation());
            preference.setPreferredSkills(houseHelp.getPreferences().getPreferredSkills());
            preference.setPreferredLanguages(houseHelp.getPreferences().getPreferredLanguages());
            preference.setPreferredChildAgeRanges(houseHelp.getPreferences().getPreferredChildAgeRanges());
            preference.setPreferredMaxChildren(houseHelp.getPreferences().getPreferredMaxChildren());
            preference.setPreferredServices(houseHelp.getPreferences().getPreferredServices());
            preference.setPreferredReligion(houseHelp.getPreferences().getPreferredReligion());
            preference.setOkayWithPets(houseHelp.getPreferences().getOkayWithPets());
            preference.setMinSalary(houseHelp.getPreferences().getMinSalary());
            preference.setMaxSalary(houseHelp.getPreferences().getMaxSalary());
            dto.setPreferences(preference);
        }

        return dto;
    }
}
