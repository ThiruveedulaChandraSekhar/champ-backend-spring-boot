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
public class FastApiRecoveryPredictionRequest {
    @JsonProperty("patient")
    private FastApiMedicineSuccessRequest.FastApiPatient patient;

    @JsonProperty("current_treatment")
    private FastApiMedicineSuccessRequest.FastApiCurrentTreatment currentTreatment;

    @JsonProperty("history")
    private FastApiMedicineSuccessRequest.FastApiHistory history = new FastApiMedicineSuccessRequest.FastApiHistory();
}
