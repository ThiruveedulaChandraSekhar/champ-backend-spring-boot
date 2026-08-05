package com.capstone.champ.service;

import com.capstone.champ.model.User;
import com.capstone.champ.payload.authentication.*;

public interface AuthenticationService {
    SignupResponse signup(SignupRequest signupRequest);
    LoginResponse login(LoginRequest loginRequest);
    UsernamesDTO getAadhaarDetailsByMobileNumber(String input);
    User getUser(String input);
    DeleteResponse deleteUser(String input);

}
