package com.example.marketplace.service;

import com.example.marketplace.dto.*;
import com.example.marketplace.enums.AgentRole;
import com.example.marketplace.enums.RequestStatus;
import com.example.marketplace.enums.WithdrawalStatus;
import com.example.marketplace.model.*;
import com.example.marketplace.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final AgentRepository agentRepository;
    private final HouseHelpRepository houseHelpRepository;
    private final UserRepository userRepository;
    private final HireRequestRepository hireRequestRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final UserService userService;

    // ─────────────────────────────────────────────────
    //  Agency CRUD
    // ─────────────────────────────────────────────────

    public AgencyResponseDTO createAgency(AgencyDTO dto) {
        Agency agency = new Agency();
        applyFields(agency, dto);
        return mapToResponseDTO(agencyRepository.save(agency));
    }

    public Page<AgencyResponseDTO> getAllAgencies(Pageable pageable) {
        return agencyRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    public AgencyResponseDTO getAgency(Long agencyId) {
        return mapToResponseDTO(findAgency(agencyId));
    }

    public AgencyResponseDTO updateAgency(Long agencyId, AgencyDTO dto) {
        Agency agency = findAgency(agencyId);
        applyFields(agency, dto);
        return mapToResponseDTO(agencyRepository.save(agency));
    }

    public AgencyResponseDTO verifyAgency(Long agencyId) {
        Agency agency = findAgency(agencyId);
        agency.setVerified(true);
        return mapToResponseDTO(agencyRepository.save(agency));
    }

    // ─────────────────────────────────────────────────
    //  My Agency (for logged-in agent)
    // ─────────────────────────────────────────────────

    public AgencyResponseDTO getMyAgency(String identifier) {
        User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhoneNumber(identifier))
                .orElseThrow(() -> new RuntimeException("User not found"));
        Agent agent = agentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No agent profile found for this user"));
        if (agent.getAgency() == null) {
            throw new RuntimeException("You are not linked to any agency yet");
        }
        return mapToResponseDTO(agent.getAgency());
    }

    // ─────────────────────────────────────────────────
    //  Members
    // ─────────────────────────────────────────────────

    /** Add an existing ROLE_AGENT user to this agency (looked up by phone). */
    @Transactional
    public AgencyResponseDTO addMember(Long agencyId, AddMemberDTO dto) {
        Agency agency = findAgency(agencyId);
        User user = userRepository.findByPhoneNumber(dto.getPhone())
                .orElseThrow(() -> new RuntimeException("No user found with phone: " + dto.getPhone()));
        Agent agent = agentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User does not have an agent profile — register them with ROLE_AGENT first"));

        AgentRole role = AgentRole.ADMIN;
        if (dto.getRole() != null) {
            try { role = AgentRole.valueOf(dto.getRole().toUpperCase()); }
            catch (IllegalArgumentException ignored) {}
        }
        agent.setAgency(agency);
        agent.setAgentRole(role);
        agentRepository.save(agent);

        return mapToResponseDTO(agency);
    }

    /** Remove a member from the agency (clears their agency link). */
    @Transactional
    public void removeMember(Long agencyId, Long agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        if (agent.getAgency() == null || !agent.getAgency().getId().equals(agencyId)) {
            throw new RuntimeException("Agent is not a member of this agency");
        }
        agent.setAgency(null);
        agentRepository.save(agent);
    }

    // ─────────────────────────────────────────────────
    //  Househelps
    // ─────────────────────────────────────────────────

    public List<AgentHouseHelpDTO> getHouseHelps(Long agencyId) {
        return houseHelpRepository.findByAgency_Id(agencyId).stream()
                .map(this::mapToHouseHelpDTO)
                .collect(Collectors.toList());
    }

    /** Register a brand-new HOUSEHELP user and assign them to this agency. */
    @Transactional
    public UserResponseDTO registerAndAssignHouseHelp(Long agencyId, RegisterRequest dto) {
        Agency agency = findAgency(agencyId);
        UserResponseDTO created = userService.registerUser(dto, "HOUSEHELP");
        userRepository.findById(created.getId()).ifPresent(user ->
                houseHelpRepository.findByUser(user).ifPresent(hh -> {
                    hh.setAgency(agency);
                    houseHelpRepository.save(hh);
                })
        );
        return created;
    }

    // ─────────────────────────────────────────────────
    //  Earnings
    // ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AgentEarningsDTO getEarnings(Long agencyId) {
        Agency agency = findAgency(agencyId);
        List<HireRequest> hireRequests = hireRequestRepository.findByAgencyId(agencyId);

        long acceptedHires = hireRequests.stream()
                .filter(hr -> hr.getStatus() == RequestStatus.ACCEPTED)
                .count();
        double totalEarned    = acceptedHires * 500.0;
        double totalWithdrawn = withdrawalRequestRepository.sumPaidByAgency(agency);
        double balance        = Math.max(0, totalEarned - totalWithdrawn);

        AgentEarningsDTO dto = new AgentEarningsDTO();
        dto.setAgentId(agencyId);
        dto.setTotalHires((int) acceptedHires);
        dto.setTotalEarned(totalEarned);
        dto.setTotalWithdrawn(totalWithdrawn);
        dto.setBalanceRemaining(balance);
        dto.setHireRequests(hireRequests.stream().map(this::mapToHireRequestDTO).collect(Collectors.toList()));
        dto.setWithdrawals(withdrawalRequestRepository.findByAgencyOrderByRequestedAtDesc(agency)
                .stream().map(this::mapToWithdrawalDTO).collect(Collectors.toList()));
        return dto;
    }

    // ─────────────────────────────────────────────────
    //  Withdrawals
    // ─────────────────────────────────────────────────

    @Transactional
    public WithdrawalRequestResponseDTO requestWithdrawal(Long agencyId, WithdrawalRequestDTO dto) {
        Agency agency = findAgency(agencyId);

        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new RuntimeException("Invalid withdrawal amount");
        }

        List<HireRequest> hireRequests = hireRequestRepository.findByAgencyId(agencyId);
        long acceptedHires = hireRequests.stream()
                .filter(hr -> hr.getStatus() == RequestStatus.ACCEPTED).count();
        double balance = Math.max(0, acceptedHires * 500.0 - withdrawalRequestRepository.sumPaidByAgency(agency));

        if (dto.getAmount() > balance) {
            throw new RuntimeException("Amount exceeds available balance of KES " + balance);
        }

        WithdrawalRequest req = new WithdrawalRequest();
        req.setAgency(agency);
        req.setAmount(dto.getAmount());
        req.setMpesaPhone(dto.getMpesaPhone());
        req.setNotes(dto.getNotes());
        req.setStatus(WithdrawalStatus.PENDING);

        return mapToWithdrawalDTO(withdrawalRequestRepository.save(req));
    }

    public List<WithdrawalRequestResponseDTO> getWithdrawals(Long agencyId) {
        Agency agency = findAgency(agencyId);
        return withdrawalRequestRepository.findByAgencyOrderByRequestedAtDesc(agency)
                .stream().map(this::mapToWithdrawalDTO).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    //  Private helpers
    // ─────────────────────────────────────────────────

    private Agency findAgency(Long id) {
        return agencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agency not found: " + id));
    }

    private void applyFields(Agency agency, AgencyDTO dto) {
        if (dto.getName()                != null) agency.setName(dto.getName());
        if (dto.getPhoneNumber()         != null) agency.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getEmail()               != null) agency.setEmail(dto.getEmail());
        if (dto.getLocationOfOperation() != null) agency.setLocationOfOperation(dto.getLocationOfOperation());
        if (dto.getHomeLocation()        != null) agency.setHomeLocation(dto.getHomeLocation());
        if (dto.getHouseNumber()         != null) agency.setHouseNumber(dto.getHouseNumber());
    }

    AgencyResponseDTO mapToResponseDTO(Agency agency) {
        AgencyResponseDTO dto = new AgencyResponseDTO();
        dto.setId(agency.getId());
        dto.setName(agency.getName());
        dto.setPhoneNumber(agency.getPhoneNumber());
        dto.setEmail(agency.getEmail());
        dto.setLocationOfOperation(agency.getLocationOfOperation());
        dto.setHomeLocation(agency.getHomeLocation());
        dto.setHouseNumber(agency.getHouseNumber());
        dto.setVerified(agency.isVerified());
        dto.setMembers(agentRepository.findByAgency_Id(agency.getId())
                .stream().map(this::mapToMemberDTO).collect(Collectors.toList()));
        return dto;
    }

    private AgencyMemberDTO mapToMemberDTO(Agent agent) {
        AgencyMemberDTO dto = new AgencyMemberDTO();
        dto.setId(agent.getId());
        dto.setVerified(agent.isVerified());
        dto.setAgentRole(agent.getAgentRole() != null ? agent.getAgentRole().name() : AgentRole.ADMIN.name());
        if (agent.getUser() != null) {
            dto.setUserId(agent.getUser().getId());
            dto.setName(agent.getUser().getName());
            dto.setEmail(agent.getUser().getEmail());
            dto.setPhoneNumber(agent.getUser().getPhoneNumber());
        }
        return dto;
    }

    private AgentHouseHelpDTO mapToHouseHelpDTO(HouseHelp hh) {
        AgentHouseHelpDTO dto = new AgentHouseHelpDTO();
        dto.setId(hh.getId());
        dto.setVerified(hh.isVerified());
        dto.setActive(hh.isActive());
        dto.setHiringStatus(hh.getHiringStatus() != null ? hh.getHiringStatus().name() : null);
        if (hh.getUser() != null) {
            dto.setUserId(hh.getUser().getId());
            dto.setName(hh.getUser().getName());
            dto.setPhone(hh.getUser().getPhoneNumber());
        }
        return dto;
    }

    private AgentHireRequestDTO mapToHireRequestDTO(HireRequest hr) {
        AgentHireRequestDTO dto = new AgentHireRequestDTO();
        dto.setId(hr.getId());
        dto.setStatus(hr.getStatus());
        dto.setCreatedAt(hr.getCreatedAt());
        dto.setStartDate(hr.getStartDate());
        if (hr.getHouseHelp() != null) {
            dto.setHouseHelpUserId(hr.getHouseHelp().getId());
            dto.setHouseHelpName(hr.getHouseHelp().getName());
        }
        if (hr.getHomeOwner() != null) {
            dto.setHomeOwnerId(hr.getHomeOwner().getId());
            if (hr.getHomeOwner().getUser() != null) {
                dto.setHomeOwnerName(hr.getHomeOwner().getUser().getName());
                dto.setHomeOwnerUserId(hr.getHomeOwner().getUser().getId());
            }
        }
        dto.setCommissionEarned(hr.getStatus() == RequestStatus.ACCEPTED ? 500.0 : 0.0);
        return dto;
    }

    private WithdrawalRequestResponseDTO mapToWithdrawalDTO(WithdrawalRequest wr) {
        WithdrawalRequestResponseDTO dto = new WithdrawalRequestResponseDTO();
        dto.setId(wr.getId());
        dto.setAmount(wr.getAmount());
        dto.setStatus(wr.getStatus());
        dto.setRequestedAt(wr.getRequestedAt());
        dto.setProcessedAt(wr.getProcessedAt());
        dto.setMpesaPhone(wr.getMpesaPhone());
        dto.setNotes(wr.getNotes());
        return dto;
    }
}
