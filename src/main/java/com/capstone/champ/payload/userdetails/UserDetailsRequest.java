package com.capstone.champ.payload.userdetails;

import com.capstone.champ.model.Address;
import com.capstone.champ.model.Allergy;
import com.capstone.champ.model.User;
import com.capstone.champ.model.Visit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsRequest {
    
    private String fullName;
    private String gender;
    private String emergencyContact;
    private String email;
    private String guardian;
    private String guardianContact;
    private LocalDate dateOfBirth;
//    private LocalDate lastUpdated;
//    private LocalDate created;
    private String bloodGroup;
//    private Boolean verificationStatus;
    
}
