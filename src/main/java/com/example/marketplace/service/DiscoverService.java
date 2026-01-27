package com.example.marketplace.service;

import com.example.marketplace.dto.HomeOwnerPreferenceDTO;
import com.example.marketplace.dto.HouseHelpCardDTO;
import com.example.marketplace.dto.HouseHelpMatchDTO;
import com.example.marketplace.model.HomeOwner;
import com.example.marketplace.model.HomeOwnerPreference;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.HomeOwnerRepository;
import com.example.marketplace.repository.HouseHelpRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.marketplace.enums.AvailabilityType;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscoverService {

    private final HouseHelpRepository houseHelpRepository;
    private final HomeOwnerRepository homeOwnerRepository;
    private final UserRepository userRepository;
    private final DistanceService distanceService;

    public List<HouseHelpMatchDTO> recommendations(String username) {

        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        HomeOwner owner = homeOwnerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

        HomeOwnerPreference pref = owner.getPreferences();

        List<HouseHelp> houseHelps = houseHelpRepository.findAllActive();

        if (pref == null) {
            return fallback(houseHelps);
        }

        return houseHelps.stream()
                .filter(h -> passesDistanceRule(owner, pref, h))
                .filter(h -> passesSalaryRule(pref, h))         // 👈 salary filtering
                .filter(h -> passesPreferenceRules(pref, h))   // 👈 other preference rules
                .map(h -> new HouseHelpMatchDTO(
                        toCard(h),
                        calculateMatch(pref, h)
                ))
                .filter(m -> m.getMatchScore() >= pref.getMinMatchScore())
                .sorted((a, b) -> b.getMatchScore() - a.getMatchScore())
                .limit(6)
                .toList();
    }

    /** Distance checking as before **/
    private boolean passesDistanceRule(HomeOwner owner,
                                       HomeOwnerPreference pref,
                                       HouseHelp help) {
        if (pref.getHouseHelpType() == null) return true;

        if (pref.getHouseHelpType() != AvailabilityType.DAYBURG &&
                pref.getHouseHelpType() != AvailabilityType.EMERGENCY_DAYBURG) {
            return true;
        }

        if (owner.getPinLocation() == null || help.getPinLocation() == null) return false;

        double distanceKm = distanceService.calculateKm(owner.getPinLocation(), help.getPinLocation());
        double maxAllowedKm = pref.getHouseHelpType() == AvailabilityType.EMERGENCY_DAYBURG ? 15 : 5;
        return distanceKm <= maxAllowedKm;
    }

    /** Salary filtering **/
    private boolean passesSalaryRule(HomeOwnerPreference pref, HouseHelp help) {
        if (pref.getPreferredMinSalary() == null &&
                pref.getPreferredMaxSalary() == null) {
            return true;
        }

        if (help.getPreferences() == null) return false;

        Double helpMin = help.getPreferences().getMinSalary();
        Double helpMax = help.getPreferences().getMaxSalary();

        if (helpMin == null || helpMax == null) return false;

        return helpMax >= pref.getPreferredMinSalary() &&
                helpMin <= pref.getPreferredMaxSalary();
    }


    /** Other preferences: pets, religion, age range, services **/
    private boolean passesPreferenceRules(HomeOwnerPreference pref, HouseHelp help) {
        // Religion
        if (pref.getReligionPreference() != null &&
                help.getReligion() != null &&
                !pref.getReligionPreference().equalsIgnoreCase(help.getReligion())) return false;

        // Pets
        if (pref.getHasPets() != null && pref.getHasPets() && (help.getPreferences() == null || !Boolean.TRUE.equals(help.getPreferences().getOkayWithPets())))
            return false;

        // Child age ranges
        if (pref.getChildrenAgeRanges() != null && !pref.getChildrenAgeRanges().isEmpty() &&
                help.getPreferences() != null && help.getPreferences().getPreferredChildAgeRanges() != null) {
            boolean match = pref.getChildrenAgeRanges().stream()
                    .anyMatch(age -> help.getPreferences().getPreferredChildAgeRanges().contains(age));
            if (!match) return false;
        }

        // Required services
        if (pref.getRequiredServices() != null && !pref.getRequiredServices().isEmpty() &&
                help.getPreferences() != null && help.getPreferences().getPreferredServices() != null) {
            boolean match = pref.getRequiredServices().stream()
                    .allMatch(service -> help.getPreferences().getPreferredServices().contains(service));
            if (!match) return false;
        }

        // Age
        if (pref.getPreferredMinAge() != null && pref.getPreferredMaxAge() != null && help.getAge() != null) {
            try {
                int age = Integer.parseInt(help.getAge());
                if (age < pref.getPreferredMinAge() || age > pref.getPreferredMaxAge()) return false;
            } catch (NumberFormatException ignored) {}
        }

        return true;
    }

    /** Match score calculation **/
    private int calculateMatch(HomeOwnerPreference p, HouseHelp h) {
        int score = 0;

        // HouseHelpType
        if (p.getHouseHelpType() == h.getHouseHelpType()) score += 30;

        // Experience
        if (h.getYearsOfExperience() != null && p.getMinExperience() != null &&
                h.getYearsOfExperience() >= p.getMinExperience()) score += 20;

        // Location
        if (p.getLocation() != null && p.getLocation().equalsIgnoreCase(h.getCurrentLocation())) score += 20;

        // Skills
        if (p.getPreferredSkills() != null && h.getSkills() != null &&
                h.getSkills().containsAll(p.getPreferredSkills())) score += 20;

        // Languages
        if (p.getPreferredLanguages() != null && h.getLanguages() != null &&
                h.getLanguages().containsAll(p.getPreferredLanguages())) score += 10;

        // Salary score
        if (p.getPreferredMinSalary() != null &&
                p.getPreferredMaxSalary() != null &&
                h.getPreferences() != null &&
                h.getPreferences().getMinSalary() != null &&
                h.getPreferences().getMaxSalary() != null) {

            double overlapMin = Math.max(
                    p.getPreferredMinSalary(),
                    h.getPreferences().getMinSalary()
            );

            double overlapMax = Math.min(
                    p.getPreferredMaxSalary(),
                    h.getPreferences().getMaxSalary()
            );

            if (overlapMin <= overlapMax) {
                score += 10;
            }
        }


        return Math.min(score, 100);
    }

    private List<HouseHelpMatchDTO> fallback(List<HouseHelp> list) {
        return list.stream()
                .limit(6)
                .map(h -> new HouseHelpMatchDTO(toCard(h), 50))
                .toList();
    }

    private HouseHelpCardDTO toCard(HouseHelp h) {
        return new HouseHelpCardDTO(
                h.getId(),
                h.getUser().getName(),
                h.getHouseHelpType(),
                h.getSkills(),
                h.isVerified(),
                h.isSecurityCleared()
        );
    }

    public HomeOwnerPreferenceDTO getPreferences(String username) {

        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        HomeOwner owner = homeOwnerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

        HomeOwnerPreference pref = owner.getPreferences();

        if (pref == null) {
            pref = new HomeOwnerPreference();
        }

        return new HomeOwnerPreferenceDTO(
                pref.getHouseHelpType(),
                pref.getMinExperience(),
                pref.getLocation(),
                pref.getPreferredSkills(),
                pref.getPreferredLanguages(),
                pref.getMinMatchScore(),
                pref.getChildrenAgeRanges(),
                pref.getNumberOfChildren(),
                pref.getRequiredServices(),
                pref.getHasPets(),
                pref.getReligionPreference(),
                pref.getRequiresSecurityCleared(),
                pref.getPreferredMinAge(),
                pref.getPreferredMaxAge(),
                pref.getPreferredMinSalary(),
                pref.getPreferredMaxSalary()
        );
    }
}
