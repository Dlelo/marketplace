package com.example.marketplace.controllers;

import com.example.marketplace.dto.HireRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hire-requests")
@RequiredArgsConstructor

public class HireRequestController {
        @PostMapping
        public ResponseEntity<?> createHireRequest(@RequestBody HireRequest hireRequest) {
            return ResponseEntity.ok().build();
        }
}