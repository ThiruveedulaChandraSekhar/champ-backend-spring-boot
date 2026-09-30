package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorMedicineFeedbackDTO {
    private Long prescriptionId;
    private String medicineName;
    private String feedback;
    private LocalDate date;
}