package com.capstone.champ.payload;

import com.capstone.champ.model.DoctorDetails;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllergyDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private Short severity;
    private DoctorDetailsDTO doctorDetails;
}
