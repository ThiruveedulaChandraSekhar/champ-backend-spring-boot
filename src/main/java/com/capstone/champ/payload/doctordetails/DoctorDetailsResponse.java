package com.capstone.champ.payload.doctordetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDetailsResponse {
    private Boolean status;
    private String message;
}
