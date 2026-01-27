package com.example.marketplace.mapper;

import com.example.marketplace.dto.ContractDTO;
import com.example.marketplace.model.Contract;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    public ContractDTO toDTO(Contract contract) {
        if (contract == null) return null;

        ContractDTO dto = new ContractDTO();
        dto.setId(contract.getId());
        dto.setHouseHelpId(contract.getHouseHelp().getId());
        dto.setHomeOwnerId(contract.getHomeOwner().getId());
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setStatus(contract.getStatus());
        dto.setAgreedRate(contract.getAgreedRate());
        dto.setTerms(contract.getTerms());
        dto.setNotes(contract.getNotes());
        dto.setCreatedAt(contract.getCreatedAt());

        dto.setHouseHelpName(contract.getHouseHelp().getUser().getName());
        dto.setHomeOwnerName(contract.getHomeOwner().getUser().getName());

        return dto;
    }

    public Contract toEntity(ContractDTO dto) {
        if (dto == null) return null;

        Contract contract = new Contract();
        contract.setId(dto.getId());
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setStatus(dto.getStatus());
        contract.setAgreedRate(dto.getAgreedRate());
        contract.setTerms(dto.getTerms());
        contract.setNotes(dto.getNotes());

        return contract;
    }
}
