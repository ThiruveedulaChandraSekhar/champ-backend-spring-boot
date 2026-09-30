package com.capstone.champ.payload.fastapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FastApiSafetyResponse {
    private Boolean safe;
    private Boolean warning;
    private String severity;
    private List<FastApiSafetyAlert> alerts = new ArrayList<>();
    private String prediction;
    private Double probability;
    private String model;
    private String message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiSafetyAlert {
        private String reason;
        private String medicine;
        private String allergy;
    }
}
