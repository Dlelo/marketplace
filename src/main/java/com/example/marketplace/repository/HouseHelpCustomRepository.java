package com.example.marketplace.repository;

import com.example.marketplace.dto.HouseHelpFilterDTO;
import com.example.marketplace.model.HouseHelp;

import java.util.List;

public interface HouseHelpCustomRepository {

    /**
     * Finds house helps using dynamic filters.
     *
     * @param filter DTO with optional fields for filtering
     * @return List of matching HouseHelp entities
     */
    List<HouseHelp> findByFilter(HouseHelpFilterDTO filter);
}
