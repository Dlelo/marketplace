package com.example.marketplace.service;

import com.example.marketplace.dto.ContractDTO;
import com.example.marketplace.enums.ContractStatus;
import com.example.marketplace.enums.HiringStatus;
import com.example.marketplace.model.Contract;
import com.example.marketplace.model.HomeOwner;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.repository.ContractRepository;
import com.example.marketplace.repository.HomeOwnerRepository;
import com.example.marketplace.repository.HouseHelpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private HouseHelpRepository houseHelpRepository;

    @Autowired
    private HomeOwnerRepository homeOwnerRepository;

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
        contract.setStatus(ContractStatus.ACTIVE);

        houseHelp.setHiringStatus(HiringStatus.HIRED);
        houseHelpRepository.save(houseHelp);

        return contractRepository.save(contract);
    }
}
