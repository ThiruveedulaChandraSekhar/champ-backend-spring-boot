package com.capstone.champ.payload.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponse {
    private Boolean status;
    private String message;
    private String aadhaarNumber;
    private String mobileNumber;
}
