package com.example.marketplace.service;

import com.example.marketplace.dto.StatsDTO;
import com.example.marketplace.enums.PaymentStatus;
import com.example.marketplace.enums.RequestStatus;
import com.example.marketplace.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserRepository userRepository;
    private final HouseHelpRepository houseHelpRepository;
    private final HomeOwnerRepository homeOwnerRepository;
    private final AgentRepository agentRepository;
    private final HireRequestRepository hireRequestRepository;
    private final PaymentRepository paymentRepository;

    public StatsDTO getStats() {
        Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        Instant monthAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        LocalDateTime weekAgoLdt = LocalDateTime.now().minusDays(7);
        LocalDateTime monthAgoLdt = LocalDateTime.now().minusDays(30);

        return StatsDTO.builder()
                // Totals
                .totalUsers(userRepository.count())
                .totalHouseHelps(houseHelpRepository.count())
                .totalHomeOwners(homeOwnerRepository.count())
                .totalAgents(agentRepository.count())
                .totalHireRequests(hireRequestRepository.count())
                .pendingHireRequests(hireRequestRepository.countByStatus(RequestStatus.PENDING))
                .acceptedHireRequests(hireRequestRepository.countByStatus(RequestStatus.ACCEPTED))
                // User trends
                .usersThisWeek(userRepository.countByCreatedAtAfter(weekAgo))
                .usersThisMonth(userRepository.countByCreatedAtAfter(monthAgo))
                .houseHelpsThisMonth(0L) // HouseHelp has no createdAt — skip
                .homeOwnersThisMonth(0L)
                // Revenue
                .totalRevenue(paymentRepository.sumRevenue())
                .revenueThisWeek(paymentRepository.sumRevenueSince(weekAgoLdt))
                .revenueThisMonth(paymentRepository.sumRevenueSince(monthAgoLdt))
                .successfulPayments(paymentRepository.countByStatus(PaymentStatus.SUCCESS))
                .pendingPayments(paymentRepository.countByStatus(PaymentStatus.PENDING))
                .build();
    }
}
