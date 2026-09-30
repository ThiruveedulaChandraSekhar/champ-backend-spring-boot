package com.capstone.champ.controller;

import com.capstone.champ.payload.authentication.LoginRequest;
import com.capstone.champ.payload.authentication.LoginResponse;
import com.capstone.champ.payload.authentication.SignupRequest;
import com.capstone.champ.payload.authentication.SignupResponse;
import com.capstone.champ.payload.authentication.UsernamesDTO;
import com.capstone.champ.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
        @Operation(summary = "Register an account", description = "Creates an unverified account using an Aadhaar number, mobile number, and password.")
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created", content = @Content(schema = @Schema(implementation = SignupResponse.class))),
            @ApiResponse(responseCode = "409", description = "Aadhaar number already exists", content = @Content(schema = @Schema(implementation = com.capstone.champ.payload.ExceptionResponse.class)))
        })
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
        return new ResponseEntity<>(authenticationService.signup(signupRequest), HttpStatus.CREATED);
    }

    @GetMapping("/aadhaar-details/{mobileNumber}")
        @Operation(summary = "Find Aadhaar numbers by mobile number", description = "Returns accounts associated with the supplied ten-digit mobile number.")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matching accounts returned", content = @Content(schema = @Schema(implementation = UsernamesDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid mobile number", content = @Content(schema = @Schema(implementation = com.capstone.champ.payload.ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Mobile number not found", content = @Content(schema = @Schema(implementation = com.capstone.champ.payload.ExceptionResponse.class)))
        })
        @Parameter(name = "mobileNumber", in = ParameterIn.PATH, required = true, description = "Ten-digit mobile number")
    public ResponseEntity<UsernamesDTO> getAadhaarDetailsByMobileNumber(@PathVariable String mobileNumber) {
        return new ResponseEntity<>(authenticationService.getAadhaarDetailsByMobileNumber(mobileNumber), HttpStatus.OK);
    }

    @PostMapping("/login")
        @Operation(summary = "Log in", description = "Authenticates using an Aadhaar number or mobile number and password.")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login result returned", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or password", content = @Content(schema = @Schema(implementation = com.capstone.champ.payload.ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(schema = @Schema(implementation = com.capstone.champ.payload.ExceptionResponse.class)))
        })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authenticationService.login(loginRequest), HttpStatus.OK);
    }

}
