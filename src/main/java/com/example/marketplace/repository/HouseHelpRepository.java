package com.example.marketplace.repository;

import com.example.marketplace.model.HouseHelp;
import com.example.marketplace.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query("""
        SELECT h FROM HouseHelp h
        WHERE h.active = true
        ORDER BY h.id DESC
    """)
    List<HouseHelp> findRecent(Pageable pageable);

    @Query("""
        SELECT h FROM HouseHelp h
        WHERE h.active = true
          AND h.verified = true
          AND h.securityCleared = true
    """)
    List<HouseHelp> findVerified(Pageable pageable);

    @Query("""
        SELECT h FROM HouseHelp h
        WHERE h.active = true
    """)
    List<HouseHelp> findAllActive();

}
