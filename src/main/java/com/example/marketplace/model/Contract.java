package com.example.marketplace.model;

import com.example.marketplace.enums.ContractStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "house_help_id", nullable = false)
    @JsonIgnoreProperties("contracts")
    private HouseHelp houseHelp;

    @ManyToOne
    @JoinColumn(name = "home_owner_id", nullable = false)
    @JsonIgnoreProperties("contracts")
    private HomeOwner homeOwner;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status = ContractStatus.PENDING;

    private Double agreedRate;

    @Column(length = 1000)
    private String terms;

    @Column(length = 500)
    private String notes;

    private LocalDate createdAt = LocalDate.now();

    public boolean isActive() {
        return status == ContractStatus.ACTIVE &&
                (endDate == null || endDate.isAfter(LocalDate.now()));
    }
}
