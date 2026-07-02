package com.example.marketplace.repository;

import com.example.marketplace.model.HouseHelpUnlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HouseHelpUnlockRepository extends JpaRepository<HouseHelpUnlock, Long> {

    boolean existsByUser_IdAndHouseHelp_Id(Long userId, Long houseHelpId);

    Optional<HouseHelpUnlock> findByUser_IdAndHouseHelp_Id(Long userId, Long houseHelpId);
}
