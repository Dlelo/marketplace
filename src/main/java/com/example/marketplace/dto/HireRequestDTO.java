package com.example.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import java.time.LocalDate;

@Data
public class HireRequestDTO {
   @NotNull()
   @JsonProperty("HouseHelpId")
   private Long HouseHelpId;

   @NotNull()
   @JsonProperty("startDate")

   private LocalDate startDate;

   @JsonProperty("message")
   private String message;
    }