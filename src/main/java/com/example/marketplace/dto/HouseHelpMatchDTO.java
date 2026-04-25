package com.example.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseHelpMatchDTO {
    private HouseHelpCardDTO houseHelp;
    private int matchScore;

    /** Whether this candidate passed all hard filters. Always true in the normal response. */
    private boolean passed = true;

    /**
     * Per-candidate notes — which criteria contributed to the score, and which
     * filters excluded them. Populated when {@code includeExcluded=true} is
     * passed on the recommendations endpoint, so the homeowner can see *why*
     * the matching engine accepted or rejected someone.
     */
    private List<String> reasons = new ArrayList<>();

    public HouseHelpMatchDTO(HouseHelpCardDTO houseHelp, int matchScore) {
        this.houseHelp = houseHelp;
        this.matchScore = matchScore;
        this.passed = true;
        this.reasons = new ArrayList<>();
    }
}
