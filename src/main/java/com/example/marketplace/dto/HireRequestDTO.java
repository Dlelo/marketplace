package com.example.marketplace.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HireRequestDTO {
   private Long HouseHelpId;
   private LocalDate startDate;
   private String message;
    }