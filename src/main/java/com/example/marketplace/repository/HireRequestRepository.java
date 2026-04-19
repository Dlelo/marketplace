package com.example.marketplace.repository;

import com.example.marketplace.dto.HouseHelpFilterDTO;
import com.example.marketplace.enums.RequestStatus;
import com.example.marketplace.model.HireRequest;
import com.example.marketplace.model.HouseHelp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HireRequestRepository extends JpaRepository<HireRequest, Long> {
    List<HireRequest> findByHouseHelp_Id(Long houseHelpId);
    List<HireRequest> findByHomeOwner_Id(Long homeOwnerUserId);
    long countByStatus(RequestStatus status);

    /** All hire requests for househelps belonging to a specific agency */
    @Query("SELECT hr FROM HireRequest hr " +
           "JOIN HouseHelp hh ON hh.user.id = hr.houseHelp.id " +
           "WHERE hh.agency.id = :agencyId " +
           "ORDER BY hr.createdAt DESC")
    List<HireRequest> findByAgencyId(@Param("agencyId") Long agencyId);

    interface HouseHelpCustomRepository {
        List<HouseHelp> findByFilter(HouseHelpFilterDTO filter);
    }
}
