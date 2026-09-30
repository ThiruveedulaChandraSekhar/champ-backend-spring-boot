package com.capstone.champ.payload;

import com.capstone.champ.model.RecoveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitRequest {
    private String reason;
    private Long diagnosisId;
    private LocalDate recoveredDate;
    private RecoveryStatus recoveryStatus;
    private LocalDateTime recoveryConfirmedAt;
    private String outcomeSource;
    private List<PrescriptionRequest> medicines;
    private List<AllergyRequest> allergies;
}
