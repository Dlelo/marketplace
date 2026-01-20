package com.example.marketplace.controllers;

import com.example.marketplace.dto.HomeOwnerPreferenceDTO;
import com.example.marketplace.service.DiscoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home-owner/preferences")
@RequiredArgsConstructor
public class HomeOwnerPreferenceController {

    private final DiscoverService discoverService;

    @GetMapping("/me")
    public ResponseEntity<HomeOwnerPreferenceDTO> myPreferences(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                discoverService.getPreferences(userDetails.getUsername())
        );
    }
}