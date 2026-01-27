package com.example.marketplace.repository;

import com.example.marketplace.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByHouseHelpId(Long houseHelpId);

    List<Contract> findByHomeOwnerId(Long homeOwnerId);

    @Query("SELECT c FROM Contract c WHERE c.houseHelp.id = :houseHelpId AND c.status = 'ACTIVE'")
    Optional<Contract> findActiveContractByHouseHelpId(Long houseHelpId);

    @Query("SELECT c FROM Contract c WHERE c.homeOwner.id = :homeOwnerId AND c.status = 'ACTIVE'")
    List<Contract> findActiveContractsByHomeOwnerId(Long homeOwnerId);
}
