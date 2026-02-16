package com.example.marketplace.mapper;

import com.example.marketplace.dto.HireRequestDTO;
import com.example.marketplace.dto.HireRequestResponseDTO;
import com.example.marketplace.dto.HomeOwnerSummaryDTO;
import com.example.marketplace.dto.HouseHelpSummaryDTO;
import com.example.marketplace.model.HireRequest;
import com.example.marketplace.model.HomeOwner;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.User;
import org.springframework.stereotype.Component;

@Component
public class HireRequestMapper {

    public HireRequest toEntity(HireRequestDTO dto, HouseHelp houseHelp) {
        HireRequest hireRequest = new HireRequest();
        hireRequest.setHouseHelp(houseHelp.getUser());
        return hireRequest;
    }

    public HireRequestResponseDTO toResponseDto(HireRequest entity) {
        HireRequestResponseDTO dto = new HireRequestResponseDTO();

        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setPaid(entity.isPaid());
        dto.setStatus(entity.getStatus());
        dto.setStartDate(entity.getStartDate());
        dto.setMessage(entity.getMessage());
        dto.setHomeOwner(mapHomeOwner(entity.getHomeOwner()));
        dto.setHouseHelp(mapHouseHelp(entity.getHouseHelp()));

        return dto;
    }

    private HomeOwnerSummaryDTO mapHomeOwner(HomeOwner homeOwner) {
        if (homeOwner == null) return null;

        HomeOwnerSummaryDTO dto = new HomeOwnerSummaryDTO();
        dto.setId(homeOwner.getId());

        User user = homeOwner.getUser();
        if (user != null) {
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhoneNumber(user.getPhoneNumber());
        }

        return dto;
    }

    private HouseHelpSummaryDTO mapHouseHelp(User houseHelp) {
        if (houseHelp == null) return null;

        HouseHelpSummaryDTO dto = new HouseHelpSummaryDTO();
        dto.setId(houseHelp.getId());
        dto.setName(houseHelp.getName());
        dto.setEmail(houseHelp.getEmail());
        dto.setPhoneNumber(houseHelp.getPhoneNumber());

        return dto;
    }
}
