package com.capstone.champ.service;

import com.capstone.champ.payload.authentication.LoginRequest;
import com.capstone.champ.payload.authentication.LoginResponse;
import com.capstone.champ.payload.authentication.SignupRequest;
import com.capstone.champ.payload.authentication.SignupResponse;

public interface AuthenticationService {
    SignupResponse signup(SignupRequest signupRequest);
    LoginResponse login(LoginRequest loginRequest);
}
