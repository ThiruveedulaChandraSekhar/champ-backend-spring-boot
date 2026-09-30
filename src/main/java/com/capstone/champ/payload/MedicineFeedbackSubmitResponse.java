package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineFeedbackSubmitResponse {
    private Boolean status;
    private String message;
    private Long prescriptionId;
    private Long medicineId;
    private String medicineName;
    private String feedback;
}