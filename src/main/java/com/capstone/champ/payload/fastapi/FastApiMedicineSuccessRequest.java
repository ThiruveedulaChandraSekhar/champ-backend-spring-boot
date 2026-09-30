package com.capstone.champ.payload.fastapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FastApiMedicineSuccessRequest {
    @JsonProperty("patient")
    private FastApiPatient patient;

    @JsonProperty("current_treatment")
    private FastApiCurrentTreatment currentTreatment;

    @JsonProperty("history")
    private FastApiHistory history = new FastApiHistory();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiPatient {
        private Integer age;
        private String gender;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiCurrentTreatment {
        private String diagnosis;
        @JsonProperty("diagnosis_code")
        private String diagnosisCode;
        private String medicine;
        @JsonProperty("active_ingredient")
        private String activeIngredient;
        private String dosage;
        private Integer duration;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiHistory {
        @JsonProperty("previous_medicines")
        private List<FastApiPreviousMedicine> previousMedicines = new ArrayList<>();

        @JsonProperty("previous_treatments")
        private List<FastApiPreviousTreatment> previousTreatments = new ArrayList<>();

        @JsonProperty("previous_visits")
        private List<FastApiPreviousVisit> previousVisits = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiPreviousMedicine {
        private String medicine;
        @JsonProperty("active_ingredient")
        private String activeIngredient;
        private String dosage;
        private Integer duration;
        private String outcome;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiPreviousTreatment {
        private String diagnosis;
        @JsonProperty("recovery_days")
        private Integer recoveryDays;
        private String outcome;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiPreviousVisit {
        private String diagnosis;
        private String medicine;
        @JsonProperty("recovery_days")
        private Integer recoveryDays;
        private String outcome;
    }
}


