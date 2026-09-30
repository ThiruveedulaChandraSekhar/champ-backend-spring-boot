package com.capstone.champ.payload.userdetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private Long id;
    private String fullName;
    private String gender;
    private String emergencyContact;
    private String email;
    private String guardian;
    private String guardianContact;
    private LocalDate dateOfBirth;
    //    private LocalDate lastUpdated;
    private LocalDateTime created;
    private String bloodGroup;
    private String accountIdentifier;
    private String mobileNumber;
    private String doorNumber;
    private String street;
    private String city;
    private String state;
    private String pinCode;
    private Boolean verificationStatus;
}
