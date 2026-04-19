package com.example.marketplace.repository;

import com.example.marketplace.model.Agency;
import com.example.marketplace.model.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {

    List<WithdrawalRequest> findByAgencyOrderByRequestedAtDesc(Agency agency);

    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM WithdrawalRequest w WHERE w.agency = :agency AND w.status = 'PAID'")
    double sumPaidByAgency(@Param("agency") Agency agency);
}
