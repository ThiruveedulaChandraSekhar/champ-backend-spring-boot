package com.capstone.champ.payload.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    private String aadhaarNumber;
    private String mobileNumber;
    private String password;
}
