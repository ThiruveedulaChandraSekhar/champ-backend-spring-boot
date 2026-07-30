package com.capstone.champ.payload.doctordetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDetailsDTO {
    private String fullName;
    private String hospitalName;
    private String specialization;
    private LocalDate dateOfBirth;
    private String gender;
    private String yearOfRegistration;
    private String medicalCouncilState;
    private String registrationNumber;
}
