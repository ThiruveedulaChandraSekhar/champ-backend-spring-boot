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
public class FastApiMedicineSuccessResponse {
    @JsonProperty("success_probability")
    private Double successProbability;
    private String prediction;
    private String model;
    @JsonProperty("model_version")
    private String modelVersion;
}
