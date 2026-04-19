package com.example.marketplace.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Standalone agency / business entity.
 * Multiple Agent users (members) belong to one agency.
 * HouseHelps and WithdrawalRequests are linked to the agency, not individual agents.
 */
@Data
@Entity
@Table(name = "agencies")
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Business / agency name */
    private String name;

    private String phoneNumber;
    private String email;
    private String locationOfOperation;
    private String homeLocation;
    private String houseNumber;

    private boolean verified = false;
}
