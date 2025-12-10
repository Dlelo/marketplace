package com.example.marketplace.repository;

import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HouseHelpRepository extends JpaRepository<HouseHelp, Long>, JpaSpecificationExecutor<HouseHelp> {

    /**
     * Find a house help by the associated User entity.
     */
    Optional<HouseHelp> findByUser(User user);

    /**
     * Find a house help by the email of the associated user.
     */
    Optional<HouseHelp> findByUser_Email(String email);

    // Optional: List<HouseHelp> findByVerifiedTrue();

    Optional<HouseHelp> findByUserAndActiveTrue(User user);

    boolean existsByUserAndActiveTrue(User user);

    boolean existsByUser(User user);

}
