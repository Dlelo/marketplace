package com.example.marketplace.repository;

import com.example.marketplace.model.HomeOwner;
import com.example.marketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomeOwnerRepository extends JpaRepository<HomeOwner, Long>, JpaSpecificationExecutor<HomeOwner> {

    Optional<HomeOwner> findByUser(User user);

    Optional<HomeOwner> findByUserAndActiveTrue(User user);

    boolean existsByUserAndActiveTrue(User user);

    Optional<HomeOwner> findByUser_Email(String email);

    boolean existsByUser(User user);
}
