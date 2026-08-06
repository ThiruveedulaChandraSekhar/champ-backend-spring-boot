package com.capstone.champ.payload;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitDTO {
    private Long id;
    private String reason;
    private LocalDate issueDate;
    private LocalDate recoveredDate;
    private List<MedicineDTO> medicines;
    private List<AllergyDTO> allergies;
    private DoctorDetailsDTO doctorDetails;
}
