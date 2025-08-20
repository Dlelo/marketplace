package com.example.marketplace.dto;

import com.example.marketplace.model.HomeOwner;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HomeOwnerUpdateResponseDTO {
    private HomeOwner updatedHomeOwner;
    private List<String> missingFields;
}
