package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineWithFeedback {
    private String medicineName;
    private String feedback;
}
