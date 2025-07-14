package com.example.marketplace.controllers;

import com.example.marketplace.dto.InterviewDTO;
import com.example.marketplace.enums.InterviewStatus;
import com.example.marketplace.model.Interview;
import com.example.marketplace.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {
    private final InterviewService interviewService;

    @PostMapping
    @PreAuthorize("hasRole('HOMEOWNER')")
    public ResponseEntity<Interview> scheduleInterview(@RequestBody InterviewDTO dto) {
        return ResponseEntity.ok(interviewService.scheduleInterview(dto.getHireRequestId(), dto.getScheduledTime(), dto.getMeetingDetails()));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HOMEOWNER', 'HOUSEHELP')")
    public ResponseEntity<Interview> updateInterviewStatus(@PathVariable Long id, @RequestBody InterviewStatus status) {
        return ResponseEntity.ok(interviewService.updateInterviewStatus(id, status));
    }

    @GetMapping("/househelp/{houseHelpId}")
    @PreAuthorize("hasRole('HOUSEHELP')")
    public ResponseEntity<List<Interview>> getInterviewsForHouseHelp(@PathVariable Long houseHelpId) {
        return ResponseEntity.ok(interviewService.getInterviewsForHouseHelp(houseHelpId));
    }

    @GetMapping("/homeowner/{houseOwnerId}")
    @PreAuthorize("hasRole('HOMEOWNER')")
    public ResponseEntity<List<Interview>> getInterviewsForHouseOwner(@PathVariable Long houseOwnerId) {
        return ResponseEntity.ok(interviewService.getInterviewsForHouseOwner(houseOwnerId));
    }
}