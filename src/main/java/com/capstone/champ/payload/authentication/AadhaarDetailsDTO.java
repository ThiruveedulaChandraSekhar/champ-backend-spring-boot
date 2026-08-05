package com.capstone.champ.payload.authentication;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AadhaarDetailsDTO {
    private String aadhaarNumber;
    private String fullName;
}
