package com.example.marketplace.repository;

import com.example.marketplace.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByHireRequest_HouseHelp_Id(Long houseHelpId);
    List<Interview> findByHireRequest_HouseOwner_Id(Long houseOwnerId);
}