package com.example.marketplace.controllers;

import com.example.marketplace.dto.HomeOwnerPreferenceDTO;
import com.example.marketplace.dto.HouseHelpMatchDTO;
import com.example.marketplace.service.DiscoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home-owner/preferences")
@RequiredArgsConstructor
public class HomeOwnerPreferenceController {

    private final DiscoverService discoverService;

    /**
     * Returns the logged-in home owner's saved preferences
     */
    @GetMapping("/preferences/me")
    public ResponseEntity<HomeOwnerPreferenceDTO> myPreferences(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HomeOwnerPreferenceDTO preferences = discoverService.getPreferences(userDetails.getUsername());
        return ResponseEntity.ok(preferences);
    }

    /**
     * Returns recommended house helps for the home owner.
     * <p>
     * When {@code includeExcluded=true}, returns *every* evaluated candidate
     * (passed and excluded) with per-candidate {@code reasons} explaining the
     * score and why each filter accepted or rejected them — useful while
     * tuning preferences and explaining empty result sets to homeowners.
     */
    @GetMapping("/househelps/recommendations")
    public ResponseEntity<List<HouseHelpMatchDTO>> recommendedHouseHelps(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "includeExcluded", defaultValue = "false") boolean includeExcluded
    ) {
        List<HouseHelpMatchDTO> recommendations =
                discoverService.recommendations(userDetails.getUsername(), includeExcluded);
        return ResponseEntity.ok(recommendations);
    }
}