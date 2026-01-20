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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscoverService {

    private final HouseHelpRepository houseHelpRepository;
    private final HomeOwnerRepository homeOwnerRepository;
    private final UserRepository userRepository;

    public List<HouseHelpMatchDTO> recommendations(String username) {

        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        HomeOwner owner = homeOwnerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

        HomeOwnerPreference pref = owner.getPreferences();

        List<HouseHelp> houseHelps = houseHelpRepository.findAllActive();

        // 🔁 Fallback
        if (pref == null) {
            return fallback(houseHelps);
        }

        return houseHelps.stream()
                .map(h -> new HouseHelpMatchDTO(
                        toCard(h),
                        calculateMatch(pref, h)
                ))
                .filter(m -> m.getMatchScore() >= pref.getMinMatchScore())
                .sorted((a, b) -> b.getMatchScore() - a.getMatchScore())
                .limit(6)
                .toList();
    }

    public List<HouseHelpCardDTO> recent(int size) {
        return houseHelpRepository.findRecent(PageRequest.of(0, size))
                .stream()
                .map(this::toCard)
                .toList();
    }

    public List<HouseHelpCardDTO> verified(int size) {
        return houseHelpRepository.findVerified(PageRequest.of(0, size))
                .stream()
                .map(this::toCard)
                .toList();
    }

    /* ---------- HELPERS ---------- */

    private int calculateMatch(HomeOwnerPreference p, HouseHelp h) {
        int score = 0;

        if (p.getHouseHelpType() == h.getHouseHelpType()) score += 30;

        if (h.getYearsOfExperience() != null &&
                p.getMinExperience() != null &&
                h.getYearsOfExperience() >= p.getMinExperience()) score += 20;

        if (p.getLocation() != null &&
                p.getLocation().equalsIgnoreCase(h.getCurrentLocation())) score += 20;

        if (p.getPreferredSkills() != null &&
                h.getSkills() != null &&
                h.getSkills().containsAll(p.getPreferredSkills())) score += 20;

        if (p.getPreferredLanguages() != null &&
                h.getLanguages() != null &&
                h.getLanguages().containsAll(p.getPreferredLanguages())) score += 10;

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
                pref.getMinMatchScore()
        );
    }

}

