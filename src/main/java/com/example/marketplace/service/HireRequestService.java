package com.example.marketplace.service;

import com.example.marketplace.dto.HireRequestDTO;
import com.example.marketplace.dto.HireRequestResponseDTO;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HireRequestService {
    private final HireRequestRepository hireRequestRepository;
    private final UserRepository userRepository;
    private final HouseHelpRepository houseHelpRepository;

    public HireRequestResponseDTO createHireRequest(HireRequestDTO hireRequestDTO, User houseOwner) {
        HouseHelp houseHelp = houseHelpRepository.findById(hireRequestDTO.getHouseHelpId())
                .orElseThrow(() -> new IllegalArgumentException("HouseHelp not found"));
        if (!houseHelp.isVerified()) {
            throw new IllegalArgumentException("HouseHelp must be verified before hiring");
        }

        HireRequest hireRequest = new HireRequest();
        hireRequest.setHouseOwner(houseOwner);
        hireRequest.setHouseHelp(houseHelp.getUser());
        hireRequest.setStatus(RequestStatus.PENDING);
        HireRequest savedRequest = hireRequestRepository.save(hireRequest);

        HireRequestResponseDTO response = new HireRequestResponseDTO();
        response.setId(savedRequest.getId());
        response.setHouseOwnerId(savedRequest.getHouseOwner().getId());
        response.setHouseHelpId(savedRequest.getHouseHelp().getId());
        response.setStatus(savedRequest.getStatus());
        return response;
    }

    public void updateStatus(Long id, RequestStatus status) {
        HireRequest hireRequest = hireRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("HireRequest not found"));
        hireRequest.setStatus(status);
        hireRequestRepository.save(hireRequest);
    }

    public List<HireRequestResponseDTO> getRequestsForHouseHelp(Long houseHelpId) {
        return hireRequestRepository.findByHouseHelp_Id(houseHelpId).stream()
                .map(request -> {
                    HireRequestResponseDTO dto = new HireRequestResponseDTO();
                    dto.setId(request.getId());
                    dto.setHouseOwnerId(request.getHouseOwner().getId());
                    dto.setHouseHelpId(request.getHouseHelp().getId());
                    dto.setStatus(request.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<HireRequestResponseDTO> findByHouseOwner_Id(Long houseOwnerId) {
        return hireRequestRepository.findByHouseOwner_Id(houseOwnerId).stream()
                .map(request -> {
                    HireRequestResponseDTO dto = new HireRequestResponseDTO();
                    dto.setId(request.getId());
                    dto.setHouseOwnerId(request.getHouseOwner().getId());
                    dto.setHouseHelpId(request.getHouseHelp().getId());
                    dto.setStatus(request.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public User findHouseOwnerByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}