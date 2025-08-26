package com.example.marketplace.repository;

import com.example.marketplace.model.Agent;

import com.example.marketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByUser(User user);

    Optional<Agent> findByUser_Email(String email);
}
