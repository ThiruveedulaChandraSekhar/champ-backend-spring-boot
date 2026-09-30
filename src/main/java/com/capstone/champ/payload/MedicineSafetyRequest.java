package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineSafetyRequest {
    private Long medicineId;
    private String medicineName;
    private String activeIngredient;
}
