package com.capstone.champ.payload;

import com.capstone.champ.model.RecoveryStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RecoveryUpdateRequest {
    private RecoveryStatus recoveryStatus;
    private LocalDate recoveredDate;
    private LocalDateTime recoveryConfirmedAt;
    private String outcomeSource;
}
