package com.capstone.champ.controller;

import com.capstone.champ.model.Role;
import com.capstone.champ.payload.GeneralResponse;
import com.capstone.champ.payload.authentication.DeleteResponse;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Administration")
public class AdminController {

    private final AuthenticationService authenticationService;

    @PostMapping("/verify-doctor/{input}")
    @Operation(summary = "Verify a doctor", description = "Marks the account identified by the path value as verified.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Doctor verified", content = @Content(schema = @Schema(implementation = GeneralResponse.class))), @ApiResponse(responseCode = "404", description = "Account not found")})
    @Parameter(name = "input", in = ParameterIn.PATH, required = true, description = "Doctor Aadhaar number or mobile number")
    public ResponseEntity<GeneralResponse> verifyDoctor(@PathVariable String input) {
        return new ResponseEntity<>(authenticationService.verifyUser(input), HttpStatus.OK);
    }

    @PostMapping("/verify-user/{input}")
    @Operation(summary = "Verify a user", description = "Marks the account identified by the path value as verified.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User verified", content = @Content(schema = @Schema(implementation = GeneralResponse.class))), @ApiResponse(responseCode = "404", description = "Account not found")})
    @Parameter(name = "input", in = ParameterIn.PATH, required = true, description = "User Aadhaar number or mobile number")
    public ResponseEntity<GeneralResponse> verifyUser(@PathVariable String input) {
        return new ResponseEntity<>(authenticationService.verifyUser(input), HttpStatus.OK);
    }

    @PostMapping("not-valid-user/{input}")
    @Operation(summary = "Delete an unverified user", description = "Deletes the account identified by the path value.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User deleted", content = @Content(schema = @Schema(implementation = DeleteResponse.class))), @ApiResponse(responseCode = "404", description = "Account not found")})
    @Parameter(name = "input", in = ParameterIn.PATH, required = true, description = "Account Aadhaar number or mobile number")
    public ResponseEntity<DeleteResponse> notVerifyUser(@PathVariable String input) {
        return new ResponseEntity<>(authenticationService.deleteUser(input), HttpStatus.OK);
    }

    @GetMapping("verification-pending/{input}")
    @Operation(summary = "List pending verification accounts", description = "Returns pending doctors when input is DOCTOR; otherwise returns pending users.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Pending accounts returned"), @ApiResponse(responseCode = "400", description = "Invalid account identifier")})
    @Parameter(name = "input", in = ParameterIn.PATH, required = true, description = "Use DOCTOR for pending doctors; any other value selects pending users")
    public ResponseEntity<?> getListOfUsers(@PathVariable String input) {
        if(input.equals(Role.DOCTOR.toString()))
            return new ResponseEntity<>(authenticationService.getPendingDoctors(), HttpStatus.OK);
        return new ResponseEntity<>(authenticationService.getPendingUsers(), HttpStatus.OK);
    }
}
