package com.example.marketplace.service;

import com.example.marketplace.enums.InterviewStatus;
import com.example.marketplace.model.HireRequest;
import com.example.marketplace.model.Interview;
import com.example.marketplace.repository.HireRequestRepository;
import com.example.marketplace.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final HireRequestRepository hireRequestRepository;

    public Interview scheduleInterview(Long hireRequestId, LocalDateTime scheduledTime, String meetingDetails) {
        HireRequest request = hireRequestRepository.findById(hireRequestId)
                .orElseThrow(() -> new RuntimeException("HireRequest not found"));
        if (!request.getStatus().equals(com.example.marketplace.enums.RequestStatus.ACCEPTED)) {
            throw new RuntimeException("Hire request must be accepted before scheduling an interview");
        }
        Interview interview = new Interview();
        interview.setHireRequest(request);
        interview.setScheduledTime(scheduledTime);
        interview.setStatus(InterviewStatus.SCHEDULED);
        interview.setMeetingDetails(meetingDetails);
        return interviewRepository.save(interview);
    }

    public Interview updateInterviewStatus(Long interviewId, InterviewStatus status) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));
        interview.setStatus(status);
        return interviewRepository.save(interview);
    }

    public List<Interview> getInterviewsForHouseHelp(Long houseHelpId) {
        return interviewRepository.findByHireRequest_HouseHelp_Id(houseHelpId);
    }

    public List<Interview> getInterviewsForHouseOwner(Long houseOwnerId) {
        return interviewRepository.findByHireRequest_HouseOwner_Id(houseOwnerId);
    }
}