package com.example.marketplace.model;

import com.example.marketplace.enums.AgentRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "agent")
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String nationalId;
    private String phoneNumber;
    private String email;
    private String locationOfOperation;
    private String homeLocation;
    private String houseNumber;
    private String idDocument;

    private boolean verified = false;

    /** Role within the agency: ADMIN can manage withdrawals & househelps, EMPLOYEE is read-only */
    @Enumerated(EnumType.STRING)
    private AgentRole agentRole = AgentRole.ADMIN;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The agency this agent member belongs to (nullable until assigned). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    @JsonIgnoreProperties({"members"})
    private Agency agency;
}
