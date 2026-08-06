package com.capstone.champ.controller;

import com.capstone.champ.payload.authentication.LoginRequest;
import com.capstone.champ.payload.authentication.SignupRequest;
import com.capstone.champ.payload.authentication.SignupResponse;
import com.capstone.champ.payload.authentication.UsernamesDTO;
import com.capstone.champ.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
        return new ResponseEntity<>(authenticationService.signup(signupRequest), HttpStatus.CREATED);
    }

    @GetMapping("/aadhaar-details/{mobileNumber}")
    public ResponseEntity<UsernamesDTO> getAadhaarDetailsByMobileNumber(@PathVariable String mobileNumber) {
        return new ResponseEntity<>(authenticationService.getAadhaarDetailsByMobileNumber(mobileNumber), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authenticationService.login(loginRequest), HttpStatus.OK);
    }
}
