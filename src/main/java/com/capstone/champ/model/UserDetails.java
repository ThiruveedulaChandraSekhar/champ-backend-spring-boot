package com.capstone.champ.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {
    private String fullName;
    private String gender;
    private String mobileNumber;
    private String emergencyContact;
    private String email;
    private String aadhaarNumber;
    private String guardian;
    private String parentContact;

}
