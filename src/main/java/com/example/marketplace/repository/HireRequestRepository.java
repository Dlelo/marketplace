package com.example.marketplace.repository;

import com.example.marketplace.dto.HouseHelpFilterDTO;
import com.example.marketplace.model.HireRequest;
import com.example.marketplace.model.HouseHelp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HireRequestRepository extends JpaRepository<HireRequest, Long> {
    List<HireRequest> findByHouseHelp_Id(Long houseHelpId);
    List<HireRequest> findByHouseOwner_Id(Long houseOwnerUserId);

    interface HouseHelpCustomRepository {

        /**
         * Finds house helps using dynamic filters.
         *
         * @param filter DTO with optional fields for filtering
         * @return List of matching HouseHelp entities
         */
        List<HouseHelp> findByFilter(HouseHelpFilterDTO filter);
    }
}