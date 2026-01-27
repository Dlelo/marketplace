package com.example.marketplace.service;

import com.example.marketplace.dto.ContractDTO;
import com.example.marketplace.enums.ContractStatus;
import com.example.marketplace.enums.HiringStatus;
import com.example.marketplace.model.Contract;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.HomeOwner;
import com.example.marketplace.repository.ContractRepository;
import com.example.marketplace.repository.HouseHelpRepository;
import com.example.marketplace.repository.HomeOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final HouseHelpRepository houseHelpRepository;
    private final HomeOwnerRepository homeOwnerRepository;

    @Transactional
    public Contract hireHouseHelp(Long houseHelpId, Long homeOwnerId, ContractDTO contractDTO) {
        HouseHelp houseHelp = houseHelpRepository.findById(houseHelpId)
                .orElseThrow(() -> new RuntimeException("HouseHelp not found"));

        HomeOwner homeOwner = homeOwnerRepository.findById(homeOwnerId)
                .orElseThrow(() -> new RuntimeException("HomeOwner not found"));

        // Check if already hired
        if (houseHelp.isCurrentlyHired()) {
            throw new IllegalStateException("HouseHelp is already hired");
        }

        Contract contract = new Contract();
        contract.setHouseHelp(houseHelp);
        contract.setHomeOwner(homeOwner);
        contract.setStartDate(contractDTO.getStartDate());
        contract.setEndDate(contractDTO.getEndDate());
        contract.setAgreedRate(contractDTO.getAgreedRate());
        contract.setTerms(contractDTO.getTerms());
        contract.setNotes(contractDTO.getNotes());
        contract.setStatus(ContractStatus.ACTIVE);

        // Update hiring status
        houseHelp.setHiringStatus(HiringStatus.HIRED);
        houseHelpRepository.save(houseHelp);

        return contractRepository.save(contract);
    }

    public Contract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
    }

    public List<Contract> getContractsByHouseHelp(Long houseHelpId) {
        return contractRepository.findByHouseHelpId(houseHelpId);
    }

    public Contract getActiveContractByHouseHelp(Long houseHelpId) {
        return contractRepository.findActiveContractByHouseHelpId(houseHelpId)
                .orElseThrow(() -> new RuntimeException("No active contract found"));
    }

    public List<Contract> getContractsByHomeOwner(Long homeOwnerId) {
        return contractRepository.findByHomeOwnerId(homeOwnerId);
    }

    public List<Contract> getActiveContractsByHomeOwner(Long homeOwnerId) {
        return contractRepository.findActiveContractsByHomeOwnerId(homeOwnerId);
    }

    @Transactional
    public Contract updateContract(Long id, ContractDTO contractDTO) {
        Contract contract = getContractById(id);

        if (contractDTO.getStartDate() != null) {
            contract.setStartDate(contractDTO.getStartDate());
        }
        if (contractDTO.getEndDate() != null) {
            contract.setEndDate(contractDTO.getEndDate());
        }
        if (contractDTO.getAgreedRate() != null) {
            contract.setAgreedRate(contractDTO.getAgreedRate());
        }
        if (contractDTO.getTerms() != null) {
            contract.setTerms(contractDTO.getTerms());
        }
        if (contractDTO.getNotes() != null) {
            contract.setNotes(contractDTO.getNotes());
        }
        if (contractDTO.getStatus() != null) {
            contract.setStatus(contractDTO.getStatus());
        }

        return contractRepository.save(contract);
    }

    @Transactional
    public Contract terminateContract(Long id) {
        Contract contract = getContractById(id);
        contract.setStatus(ContractStatus.TERMINATED);
        contract.setEndDate(LocalDate.now());

        // Update house help status
        HouseHelp houseHelp = contract.getHouseHelp();
        houseHelp.setHiringStatus(HiringStatus.AVAILABLE);
        houseHelpRepository.save(houseHelp);

        return contractRepository.save(contract);
    }

    @Transactional
    public Contract completeContract(Long id) {
        Contract contract = getContractById(id);
        contract.setStatus(ContractStatus.COMPLETED);

        // Update house help status
        HouseHelp houseHelp = contract.getHouseHelp();
        houseHelp.setHiringStatus(HiringStatus.AVAILABLE);
        houseHelpRepository.save(houseHelp);

        return contractRepository.save(contract);
    }

    @Transactional
    public void deleteContract(Long id) {
        Contract contract = getContractById(id);

        // If deleting an active contract, update house help status
        if (contract.getStatus() == ContractStatus.ACTIVE) {
            HouseHelp houseHelp = contract.getHouseHelp();
            houseHelp.setHiringStatus(HiringStatus.AVAILABLE);
            houseHelpRepository.save(houseHelp);
        }

        contractRepository.delete(contract);
    }
}
