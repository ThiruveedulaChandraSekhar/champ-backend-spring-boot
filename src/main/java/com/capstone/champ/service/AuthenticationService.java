package com.capstone.champ.service;

import com.capstone.champ.model.User;
import com.capstone.champ.payload.GeneralResponse;
import com.capstone.champ.payload.authentication.*;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.userdetails.UserDetailsDTO;

import java.util.List;

public interface AuthenticationService {
    SignupResponse signup(SignupRequest signupRequest);
    LoginResponse login(LoginRequest loginRequest);
    UsernamesDTO getAadhaarDetailsByMobileNumber(String input);
    User getUser(String input);
    DeleteResponse deleteUser(String input);
    GeneralResponse verifyUser(String aadhaarNumber);
    List<DoctorDetailsDTO> getPendingDoctors();
    List<UserDetailsDTO> getPendingUsers();
}
