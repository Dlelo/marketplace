package com.example.marketplace.service;

import com.example.marketplace.dto.HireRequestDTO;
import com.example.marketplace.dto.HireRequestResponseDTO;
import com.example.marketplace.enums.RequestStatus;
import com.example.marketplace.mapper.HireRequestMapper;
import com.example.marketplace.model.HireRequest;
import com.example.marketplace.model.HomeOwner;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.HireRequestRepository;
import com.example.marketplace.repository.HomeOwnerRepository;
import com.example.marketplace.repository.HouseHelpRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HireRequestService {

    private final HireRequestRepository hireRequestRepository;
    private final UserRepository userRepository;
    private final HomeOwnerRepository homeOwnerRepository;
    private final HouseHelpRepository houseHelpRepository;
    private final HomeOwnerService homeOwnerService;
    private final HireRequestMapper hireRequestMapper;

    /**
     * Create a new hire request after validating profile, subscription/payment, and househelp.
     */
    public HireRequestResponseDTO createHireRequest(HireRequestDTO dto, String ownerEmail) {
        var homeOwner = homeOwnerRepository.findByUser_Email(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("House owner not found with email: " + ownerEmail));

        var houseHelp = houseHelpRepository.findById(dto.getHouseHelpId())
                .orElseThrow(() -> new IllegalArgumentException("HouseHelp not found"));

        if (!houseHelp.isVerified()) {
            throw new IllegalArgumentException("HouseHelp must be verified before hiring");
        }

        HireRequest hireRequest = hireRequestMapper.toEntity(dto, houseHelp);
        hireRequest.setHomeOwner(homeOwner);
        hireRequest.setStatus(RequestStatus.PENDING);

        HireRequest saved = hireRequestRepository.save(hireRequest);
        return hireRequestMapper.toResponseDto(saved);
    }

    /**
     * Update request status (used by househelp when accepting/declining).
     */
    public void updateStatus(Long id, RequestStatus status) {
        HireRequest hireRequest = hireRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("HireRequest not found"));
        hireRequest.setStatus(status);
        hireRequestRepository.save(hireRequest);
    }

    /**
     * Get requests directed to a specific househelp.
     */
    public List<HireRequestResponseDTO> getRequestsForHouseHelp(Long houseHelpId) {
        return hireRequestRepository.findByHouseHelp_Id(houseHelpId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get requests created by a specific homeowner.
     */
    public List<HireRequestResponseDTO> findByHouseOwner_Id(Long houseOwnerId) {
        return hireRequestRepository.findByHouseOwner_Id(houseOwnerId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find homeowner by email.
     */
    public User findHouseOwnerByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Get paginated list of all hire requests.
     */
    public Page<HireRequest> getAllHireRequests(Pageable pageable) {
        return hireRequestRepository.findAll(pageable);
    }

    // -------------------- 🔹 Private Helpers -------------------- //

    private void validateHomeOwnerProfile(HomeOwner homeOwner) {
        List<String> missingFields = homeOwnerService.getMissingFields(homeOwner);
        if (!missingFields.isEmpty()) {
            throw new IllegalStateException("Profile incomplete. Missing fields: " + String.join(", ", missingFields));
        }
    }

    private void validateSubscriptionOrPayment(HomeOwner homeOwner, boolean paymentSuccess) {
        if (!paymentSuccess && !homeOwner.isSubscriptionActive()) {
            throw new IllegalStateException("You must have an active subscription or complete a payment to hire.");
        }
    }

    private HouseHelp validateHouseHelp(Long houseHelpId) {
        HouseHelp houseHelp = houseHelpRepository.findById(houseHelpId)
                .orElseThrow(() -> new IllegalArgumentException("HouseHelp not found"));
        if (!houseHelp.isVerified()) {
            throw new IllegalArgumentException("HouseHelp must be verified before hiring");
        }
        return houseHelp;
    }

    private HireRequestResponseDTO mapToResponseDTO(HireRequest hireRequest) {
        HireRequestResponseDTO dto = new HireRequestResponseDTO();
        dto.setId(hireRequest.getId());
        dto.setHomeOwnerId(hireRequest.getHomeOwner().getId());
        dto.setHouseHelpId(hireRequest.getHouseHelp().getId());
        dto.setStatus(hireRequest.getStatus());
        return dto;
    }
}
