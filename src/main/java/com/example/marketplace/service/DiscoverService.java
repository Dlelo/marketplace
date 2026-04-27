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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscoverService {

    private final HouseHelpRepository houseHelpRepository;
    private final HomeOwnerRepository homeOwnerRepository;
    private final UserRepository userRepository;
    private final DistanceService distanceService;

    /** Default minimum match score when the homeowner hasn't set one. */
    private static final int DEFAULT_MIN_MATCH_SCORE = 25;

    public List<HouseHelpMatchDTO> recommendations(String username) {
        return recommendations(username, false);
    }

    /**
     * @param includeExcluded when true, returns *every* evaluated candidate
     *     (passed and excluded) with reason notes — useful for tuning preferences
     *     and showing the homeowner why someone didn't show up.
     */
    public List<HouseHelpMatchDTO> recommendations(String username, boolean includeExcluded) {

        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        HomeOwner owner = homeOwnerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

        HomeOwnerPreference pref = owner.getPreferences();
        List<HouseHelp> houseHelps = houseHelpRepository.findAllActive();

        if (pref == null) {
            return fallback(houseHelps, includeExcluded);
        }

        int minScore = pref.getMinMatchScore() != null ? pref.getMinMatchScore() : DEFAULT_MIN_MATCH_SCORE;

        List<HouseHelpMatchDTO> evaluated = new ArrayList<>();
        for (HouseHelp h : houseHelps) {
            List<String> reasons = new ArrayList<>();
            boolean passed = true;

            if (!passesDistanceRule(owner, pref, h)) {
                passed = false;
                reasons.add("Excluded: outside the allowed distance for the requested availability type");
            }
            if (!passesSecurityRule(pref, h, reasons)) {
                passed = false;
            }
            if (!passesChildCapacityRule(pref, h, reasons)) {
                passed = false;
            }
            if (!passesSalaryRule(pref, h, reasons)) {
                passed = false;
            }
            if (!passesPreferenceRules(pref, h, reasons)) {
                passed = false;
            }
            if (!passesHouseHelpSidePreferences(pref, h, reasons)) {
                passed = false;
            }

            int score = calculateMatch(pref, h, reasons);
            if (passed && score < minScore) {
                passed = false;
                reasons.add("Excluded: match score " + score + " is below the minimum (" + minScore + ")");
            }

            HouseHelpMatchDTO dto = new HouseHelpMatchDTO(toCard(h), score);
            dto.setPassed(passed);
            dto.setReasons(reasons);
            evaluated.add(dto);
        }

        evaluated.sort(Comparator.comparingInt(HouseHelpMatchDTO::getMatchScore).reversed());

        if (includeExcluded) {
            return evaluated;
        }
        return evaluated.stream()
                .filter(HouseHelpMatchDTO::isPassed)
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

    /** Hard filter: homeowner requires a security-cleared house help. */
    private boolean passesSecurityRule(HomeOwnerPreference pref, HouseHelp help, List<String> reasons) {
        if (Boolean.TRUE.equals(pref.getRequiresSecurityCleared()) && !help.isSecurityCleared()) {
            reasons.add("Excluded: not security cleared");
            return false;
        }
        return true;
    }

    /** Hard filter: house help can only handle up to N children. */
    private boolean passesChildCapacityRule(HomeOwnerPreference pref, HouseHelp help, List<String> reasons) {
        Integer myKids = pref.getNumberOfChildren();
        Integer maxKids = help.getPreferences() != null ? help.getPreferences().getPreferredMaxChildren() : null;
        if (myKids != null && maxKids != null && myKids > maxKids) {
            reasons.add(String.format(
                    "Excluded: only handles up to %d children (you have %d)",
                    maxKids, myKids));
            return false;
        }
        return true;
    }

    /**
     * Hard-filter the house help's own side preferences against the homeowner.
     * Today this checks the house help's preferred religion vs the homeowner's
     * stated religion preference — a two-way handshake feels less surprising
     * than a one-way constraint.
     */
    private boolean passesHouseHelpSidePreferences(HomeOwnerPreference pref, HouseHelp help, List<String> reasons) {
        if (help.getPreferences() == null) return true;
        String hhReligionWant = help.getPreferences().getPreferredReligion();
        String hoReligion = pref.getReligionPreference();
        if (hhReligionWant != null && !hhReligionWant.isBlank() &&
                hoReligion != null && !hoReligion.isBlank() &&
                !hhReligionWant.equalsIgnoreCase(hoReligion)) {
            reasons.add("Excluded: house help prefers a different employer religion");
            return false;
        }
        return true;
    }

    /** Salary filtering — appends an exclusion note to {@code reasons} on miss. */
    private boolean passesSalaryRule(HomeOwnerPreference pref, HouseHelp help, List<String> reasons) {
        if (pref.getPreferredMinSalary() == null && pref.getPreferredMaxSalary() == null) return true;

        if (help.getPreferences() == null) {
            reasons.add("Excluded: house help hasn't set salary expectations");
            return false;
        }

        Double helpMin = help.getPreferences().getMinSalary();
        Double helpMax = help.getPreferences().getMaxSalary();

        if (helpMin == null || helpMax == null) {
            reasons.add("Excluded: house help hasn't set both salary bounds");
            return false;
        }

        boolean overlap = helpMax >= pref.getPreferredMinSalary() && helpMin <= pref.getPreferredMaxSalary();
        if (!overlap) {
            reasons.add(String.format(
                    "Excluded: salary range KES %.0f–%.0f doesn't overlap your KES %.0f–%.0f",
                    helpMin, helpMax,
                    pref.getPreferredMinSalary(), pref.getPreferredMaxSalary()
            ));
        }
        return overlap;
    }

    /** Other preferences: pets, religion, age range, services. */
    private boolean passesPreferenceRules(HomeOwnerPreference pref, HouseHelp help, List<String> reasons) {
        boolean ok = true;

        // Religion
        if (pref.getReligionPreference() != null &&
                help.getReligion() != null &&
                !pref.getReligionPreference().equalsIgnoreCase(help.getReligion())) {
            reasons.add("Excluded: religion preference (" + pref.getReligionPreference() + ") doesn't match");
            ok = false;
        }

        // Pets
        if (pref.getHasPets() != null && pref.getHasPets() &&
                (help.getPreferences() == null || !Boolean.TRUE.equals(help.getPreferences().getOkayWithPets()))) {
            reasons.add("Excluded: house help isn't okay with pets");
            ok = false;
        }

        // Uniform — hard filter when the homeowner requires one
        if (Boolean.TRUE.equals(pref.getRequiresUniform()) &&
                (help.getPreferences() == null || !Boolean.TRUE.equals(help.getPreferences().getOkayWithUniform()))) {
            reasons.add("Excluded: house help isn't willing to wear a uniform");
            ok = false;
        }

        // Child age ranges
        if (pref.getChildrenAgeRanges() != null && !pref.getChildrenAgeRanges().isEmpty() &&
                help.getPreferences() != null && help.getPreferences().getPreferredChildAgeRanges() != null) {
            boolean match = pref.getChildrenAgeRanges().stream()
                    .anyMatch(age -> help.getPreferences().getPreferredChildAgeRanges().contains(age));
            if (!match) {
                reasons.add("Excluded: doesn't cover your children's age ranges");
                ok = false;
            }
        }

        // Required services
        if (pref.getRequiredServices() != null && !pref.getRequiredServices().isEmpty() &&
                help.getPreferences() != null && help.getPreferences().getPreferredServices() != null) {
            boolean match = pref.getRequiredServices().stream()
                    .allMatch(service -> help.getPreferences().getPreferredServices().contains(service));
            if (!match) {
                reasons.add("Excluded: doesn't offer all required services");
                ok = false;
            }
        }

        // Age
        if (pref.getPreferredMinAge() != null && pref.getPreferredMaxAge() != null && help.getAge() != null) {
            try {
                int age = Integer.parseInt(help.getAge());
                if (age < pref.getPreferredMinAge() || age > pref.getPreferredMaxAge()) {
                    reasons.add(String.format(
                            "Excluded: age %d is outside your preferred %d–%d",
                            age, pref.getPreferredMinAge(), pref.getPreferredMaxAge()));
                    ok = false;
                }
            } catch (NumberFormatException ignored) {}
        }

        return ok;
    }

    /** Score calculation — appends "+N for X" notes for each contributing criterion. */
    private int calculateMatch(HomeOwnerPreference p, HouseHelp h, List<String> reasons) {
        int score = 0;

        if (p.getHouseHelpType() == h.getHouseHelpType()) {
            score += 30;
            reasons.add("+30 availability type matches");
        }

        if (h.getYearsOfExperience() != null && p.getMinExperience() != null &&
                h.getYearsOfExperience() >= p.getMinExperience()) {
            score += 20;
            reasons.add("+20 meets minimum experience");
        }

        if (p.getLocation() != null && p.getLocation().equalsIgnoreCase(h.getCurrentLocation())) {
            score += 20;
            reasons.add("+20 same current location");
        }

        if (p.getPreferredSkills() != null && !p.getPreferredSkills().isEmpty() &&
                h.getSkills() != null && h.getSkills().containsAll(p.getPreferredSkills())) {
            score += 20;
            reasons.add("+20 has all preferred skills");
        }

        if (p.getPreferredLanguages() != null && !p.getPreferredLanguages().isEmpty() &&
                h.getLanguages() != null && h.getLanguages().containsAll(p.getPreferredLanguages())) {
            score += 10;
            reasons.add("+10 speaks all preferred languages");
        }

        if (p.getPreferredMinSalary() != null && p.getPreferredMaxSalary() != null &&
                h.getPreferences() != null &&
                h.getPreferences().getMinSalary() != null &&
                h.getPreferences().getMaxSalary() != null) {

            double overlapMin = Math.max(p.getPreferredMinSalary(), h.getPreferences().getMinSalary());
            double overlapMax = Math.min(p.getPreferredMaxSalary(), h.getPreferences().getMaxSalary());

            if (overlapMin <= overlapMax) {
                score += 10;
                reasons.add("+10 salary expectation overlaps");
            }
        }

        // House help's own preferred location matches homeowner's location
        if (p.getLocation() != null && h.getPreferences() != null &&
                p.getLocation().equalsIgnoreCase(h.getPreferences().getPreferredLocation())) {
            score += 5;
            reasons.add("+5 house help prefers your location too");
        }

        // Languages overlap (any-of) — partial match also rewarded
        if (p.getPreferredLanguages() != null && !p.getPreferredLanguages().isEmpty() &&
                h.getLanguages() != null) {
            long overlap = h.getLanguages().stream()
                    .filter(p.getPreferredLanguages()::contains)
                    .count();
            if (overlap > 0 && overlap < p.getPreferredLanguages().size()) {
                score += 3;
                reasons.add("+3 speaks some of your preferred languages");
            }
        }

        // Skill overlap (partial)
        if (p.getPreferredSkills() != null && !p.getPreferredSkills().isEmpty() &&
                h.getSkills() != null) {
            long overlap = h.getSkills().stream()
                    .filter(p.getPreferredSkills()::contains)
                    .count();
            if (overlap > 0 && overlap < p.getPreferredSkills().size()) {
                score += 5;
                reasons.add("+5 has some of your preferred skills");
            }
        }

        // Child age range overlap — bonus when the house help's preferred ranges
        // explicitly include yours (already a hard filter, but reward exact fit)
        if (p.getChildrenAgeRanges() != null && !p.getChildrenAgeRanges().isEmpty() &&
                h.getPreferences() != null && h.getPreferences().getPreferredChildAgeRanges() != null &&
                h.getPreferences().getPreferredChildAgeRanges().containsAll(p.getChildrenAgeRanges())) {
            score += 5;
            reasons.add("+5 covers all your children's age ranges");
        }

        // Required services overlap (already a hard filter for "all", but reward partial)
        if (p.getRequiredServices() != null && !p.getRequiredServices().isEmpty() &&
                h.getPreferences() != null && h.getPreferences().getPreferredServices() != null) {
            long overlap = h.getPreferences().getPreferredServices().stream()
                    .filter(p.getRequiredServices()::contains)
                    .count();
            if (overlap > 0 && overlap < p.getRequiredServices().size()) {
                score += 3;
                reasons.add("+3 offers some required services");
            }
        }

        // Verified house help — small trust bonus
        if (h.isVerified()) {
            score += 3;
            reasons.add("+3 verified profile");
        }

        // Security cleared — bonus when not strictly required
        if (h.isSecurityCleared() && !Boolean.TRUE.equals(p.getRequiresSecurityCleared())) {
            score += 2;
            reasons.add("+2 security cleared");
        }

        return Math.min(score, 100);
    }

    private List<HouseHelpMatchDTO> fallback(List<HouseHelp> list, boolean includeAll) {
        List<HouseHelpMatchDTO> dtos = list.stream()
                .map(h -> {
                    HouseHelpMatchDTO dto = new HouseHelpMatchDTO(toCard(h), 50);
                    dto.getReasons().add("Fallback score — homeowner has no preferences set");
                    return dto;
                })
                .toList();
        return includeAll ? dtos : dtos.stream().limit(6).toList();
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
                pref.getRequiresUniform(),
                pref.getPreferredMinAge(),
                pref.getPreferredMaxAge(),
                pref.getPreferredMinSalary(),
                pref.getPreferredMaxSalary()
        );
    }
}
