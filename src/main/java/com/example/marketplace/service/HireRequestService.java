package com.example.marketplace.service;

import com.example.marketplace.dto.HireRequestDTO;
import com.example.marketplace.enums.RequestStatus;
import com.example.marketplace.model.HireRequest;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.HireRequestRepository;
import com.example.marketplace.repository.HouseHelpRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HireRequestService {
    private final HireRequestRepository hireRequestRepository;
    private final HouseHelpRepository houseHelpRepository;
    private final UserRepository userRepository;

    public HireRequest createHireRequest(HireRequestDTO dto, User houseOwner) {
        HouseHelp houseHelp = houseHelpRepository.findById(dto.getHouseHelpId())
                .orElseThrow(() -> new RuntimeException("HouseHelp not found"));
        if (!houseHelp.getVerified()) {
            throw new RuntimeException("HouseHelp is not verified");
        }
        HireRequest request = new HireRequest();
        request.setHouseOwner(houseOwner);
        request.setHouseHelp(houseHelp.getUser());
        request.setStatus(RequestStatus.PENDING);
        request.setStartDate(dto.getStartDate());
        request.setMessage(dto.getMessage());
        return hireRequestRepository.save(request);
    }

    public void updateStatus(Long requestId, RequestStatus status) {
        HireRequest request = hireRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        hireRequestRepository.save(request);
    }

    public List<HireRequest> getRequestsForHouseHelp(Long houseHelpId) {
        return hireRequestRepository.findByHouseHelp_Id(houseHelpId);
    }

    public List<HireRequest> findByHouseOwner_Id(Long houseOwnerId) {
        return hireRequestRepository.findByHouseOwner_Id(houseOwnerId);
    }

    public User findHouseOwnerByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("HouseOwner not found"));
    }
}