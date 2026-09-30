package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineFeedbackRequest {
    private Long prescriptionId;
    private Long medicineId;
    private String feedback;
}