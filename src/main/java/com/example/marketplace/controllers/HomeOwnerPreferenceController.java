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
     * Returns recommended househelps based on the home owner's preferences
     */
    @GetMapping("/househelps/recommendations")
    public ResponseEntity<List<HouseHelpMatchDTO>> recommendedHouseHelps(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<HouseHelpMatchDTO> recommendations = discoverService.recommendations(userDetails.getUsername());
        return ResponseEntity.ok(recommendations);
    }
}