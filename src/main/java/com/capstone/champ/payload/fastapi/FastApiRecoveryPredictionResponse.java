package com.capstone.champ.payload.fastapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FastApiRecoveryPredictionResponse {
    @JsonProperty("estimated_recovery_days")
    private Double estimatedRecoveryDays;
    private FastApiRecoveryRange estimatedRange;
    private String model;
    @JsonProperty("model_version")
    private String modelVersion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiRecoveryRange {
        @JsonProperty("minimum_days")
        private Integer minimumDays;
        @JsonProperty("maximum_days")
        private Integer maximumDays;
    }
}
