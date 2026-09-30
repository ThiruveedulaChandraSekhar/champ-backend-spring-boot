package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientHistorySummaryResponse {
    private Long patientId;
    private String patientName;
    private Integer totalVisits;
    private List<String> diagnoses = new ArrayList<>();
    private List<String> medicines = new ArrayList<>();
    private List<String> allergies = new ArrayList<>();
    private List<RecentVisitSummary> recentVisits = new ArrayList<>();
    private RecentVisitSummary latestVisit;
    private List<String> importantInformation = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecentVisitSummary {
        private Long visitId;
        private String reason;
        private String diagnosis;
        private LocalDate issueDate;
        private String recoveryStatus;
        private Double predictedRecoveryDays;
        private List<String> prescriptionMedicines = new ArrayList<>();
    }
}
