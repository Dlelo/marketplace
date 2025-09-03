package com.example.marketplace.mapper;

import com.example.marketplace.dto.HireRequestDTO;
import com.example.marketplace.dto.HireRequestResponseDTO;
import com.example.marketplace.model.HireRequest;
import com.example.marketplace.model.HouseHelp;
import org.springframework.stereotype.Component;

@Component
public class HireRequestMapper {

    public HireRequest toEntity(HireRequestDTO dto, HouseHelp houseHelp) {
        HireRequest hireRequest = new HireRequest();
        hireRequest.setHouseHelp(houseHelp.getUser()); // assuming HireRequest links to User of HouseHelp
        return hireRequest;
    }

    public HireRequestResponseDTO toResponseDto(HireRequest entity) {
        HireRequestResponseDTO dto = new HireRequestResponseDTO();
        dto.setId(entity.getId());
        dto.setHomeOwnerId(entity.getHomeOwner().getId());
        dto.setHouseHelpId(entity.getHouseHelp().getId());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
