package com.example.marketplace.dto;
import lombok.Data;

@Data
public class AgentUpdateDTO {
    private String agencyName;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String nationalId;
    private String idDocument;
    private String locationOfOperation;
    private String homeLocation;
    private String houseNumber;
   }
