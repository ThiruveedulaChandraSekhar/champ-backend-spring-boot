package com.capstone.champ.payload;

import com.capstone.champ.model.Allergy;
import com.capstone.champ.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitRequest {
    private String reason;
    private List<Medicine> medicines;
    private List<Allergy> allergies;
}
