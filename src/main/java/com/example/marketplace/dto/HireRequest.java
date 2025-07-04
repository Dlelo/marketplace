package com.example.marketplace.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HireRequest {
   private Long SellerId;
   private LocalDate startDate;
   private String message;
    }