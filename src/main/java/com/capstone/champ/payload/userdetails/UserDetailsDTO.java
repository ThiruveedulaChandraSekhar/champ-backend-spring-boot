package com.capstone.champ.payload.userdetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private String fullName;
    private String gender;
    private String emergencyContact;
    private String email;
    private String guardian;
    private String guardianContact;
    private LocalDate dateOfBirth;
    //    private LocalDate lastUpdated;
    private LocalDate created;
    private String bloodGroup;
}
