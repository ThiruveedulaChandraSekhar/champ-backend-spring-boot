package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MedicineSafetyCheckResponse {
    private Boolean safe;
    private Boolean warning;
    private String medicine;
    private List<MatchedAllergy> matchedAllergies = new ArrayList<>();
    private String message;
    private String prediction;
    private Double probability;
    private String model;

    public MedicineSafetyCheckResponse(Boolean safe, Boolean warning, String medicine,
                                       List<MatchedAllergy> matchedAllergies, String message) {
        this.safe = safe;
        this.warning = warning;
        this.medicine = medicine;
        this.matchedAllergies = matchedAllergies;
        this.message = message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MatchedAllergy {
        private String allergen;
        private String severity;
        private String source;
    }
}
