package com.example.marketplace.service;

import com.example.marketplace.dto.HouseHelpFilterDTO;
import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.repository.HouseHelpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseHelpService {
    private final HouseHelpRepository houseHelpRepository;

    public HouseHelp verifyHouseHelp(Long houseHelpId) {
        HouseHelp houseHelp = houseHelpRepository.findById(houseHelpId)
                .orElseThrow(() -> new RuntimeException("HouseHelp not found"));
        houseHelp.setVerified(true);
        return houseHelpRepository.save(houseHelp);
    }

    public List<HouseHelp> findByFilter(HouseHelpFilterDTO filter) {
        return houseHelpRepository.findByFilter(filter);
    }
}