package com.example.marketplace.model;

import com.example.marketplace.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "house_help")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class HouseHelp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"houseHelp", "homeOwner", "roles"})
    private User user;

    @Column(nullable = false)
    private boolean verified;

    private Integer numberOfChildren;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Languages> languages;

    private String levelOfEducation;
    private String contactPersons;
    private String homeLocation;

    @Enumerated(EnumType.STRING)
    private CountyOptions homeCounty;

    private String currentLocation;
    @Enumerated(EnumType.STRING)
    private CountyOptions currentCounty;

    private String nationalId;
    private String nationalIdDocument;
    private String profilePictureDocument;
    private String contactPersonsPhoneNumber;
    private String medicalReport;
    private String goodConduct;
    private Integer yearsOfExperience;
    private String religion;
    private String height;
    private String weight;
    private String age;
    private String gender;
    private String localAuthorityVerificationDocument;
    private AvailabilityType houseHelpType;
    private String availability;

    @Column(length = 1000)
    private String experienceSummary;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<ChildAgeRange> childAgeRanges;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<CareService> services;

    private Integer maxChildren;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Skills> skills;

    private boolean active = true;
    private boolean securityCleared = false;
    private String securityClearanceComments;

    @Embedded
    private HouseHelpPreference preferences;


    public int getCountySurcharge() {
        if (currentCounty == null) {
            return 0;
        }
        return currentCounty.getSurchargeAmount();
    }


    public boolean isInNairobi() {
        return currentCounty != null && currentCounty.isNairobi();
    }


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "house_help_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "house_help_lng")),
            @AttributeOverride(name = "placeName", column = @Column(name = "house_help_place")),
            @AttributeOverride(name = "addressLine", column = @Column(name = "house_help_address"))
    })
    private GeoLocation pinLocation;

    private Integer maxTravelDistanceKm;

}
