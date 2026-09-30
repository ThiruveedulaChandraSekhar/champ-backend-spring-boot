package com.capstone.champ.payload.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Boolean status;
    private String message;
    private String input;
    private String role;
    private Boolean verificationStatus;
    private String accountIdentifier;
    private String mobileNumber;
    private String fullName;
    private Long profileId;
}
