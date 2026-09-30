package com.capstone.champ.payload.authentication;

import com.capstone.champ.payload.doctordetails.DoctorDetailsRequest;
import com.capstone.champ.payload.userdetails.AddressRequest;
import com.capstone.champ.payload.userdetails.UserDetailsRequest;
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
    private String confirmPassword;
    private String role;
    private UserDetailsRequest patientDetails;
    private AddressRequest address;
    private DoctorDetailsRequest doctorDetails;
}
