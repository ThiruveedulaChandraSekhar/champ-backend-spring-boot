package com.capstone.champ.controller;

import com.capstone.champ.payload.AllergyResponse;
import com.capstone.champ.payload.MedicineFeedBackResponse;
import com.capstone.champ.payload.MedicineFeedbackRequest;
import com.capstone.champ.payload.MedicineFeedbackSubmitResponse;
import com.capstone.champ.payload.MedicineSafetyCheckResponse;
import com.capstone.champ.payload.MedicineSafetyRequest;
import com.capstone.champ.payload.PatientHistorySummaryResponse;
import com.capstone.champ.payload.GeneralResponse;
import com.capstone.champ.payload.RecoveryUpdateRequest;
import com.capstone.champ.payload.VisitResponse;
import com.capstone.champ.payload.userdetails.UserDetailsDTO;
import com.capstone.champ.payload.userdetails.UserDetailsRequest;
import com.capstone.champ.payload.userdetails.UserDetailsResponse;
import com.capstone.champ.service.UserService;
import com.capstone.champ.configuration.AccountOwnershipInterceptor;
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
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @PostMapping("/details/{input}")
    @Operation(summary = "Create patient details", description = "Adds personal details to the account identified by the path value.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Details created", content = @Content(schema = @Schema(implementation = UserDetailsResponse.class))), @ApiResponse(responseCode = "404", description = "Account not found")})
    @Parameter(name = "input", in = ParameterIn.PATH, required = true, description = "Account Aadhaar number")
    public ResponseEntity<UserDetailsResponse> addUserDetails(@PathVariable String input, @RequestBody UserDetailsRequest userDetailsRequest) {
        return new ResponseEntity<>(userService.addUserDetails(input, userDetailsRequest), HttpStatus.CREATED);
    }

    @PutMapping("/details")
    @Operation(summary = "Update patient details", description = "Updates the stored patient details identified by the request DTO id.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Details updated", content = @Content(schema = @Schema(implementation = UserDetailsResponse.class))), @ApiResponse(responseCode = "404", description = "Details not found")})
    public ResponseEntity<UserDetailsResponse> updateUserDetails(@RequestBody UserDetailsDTO userDetailsDTO,
            @RequestHeader(AccountOwnershipInterceptor.ACCOUNT_HEADER) String accountId) {
        if (userDetailsDTO.getAccountIdentifier() == null || !accountId.equals(userDetailsDTO.getAccountIdentifier()))
            throw new com.capstone.champ.exception.InvalidInputException("Patient account does not own this profile");
        return new ResponseEntity<>(userService.updateUserDetails(userDetailsDTO), HttpStatus.OK);
    }

    @GetMapping("/details/{input}")
    @Operation(summary = "Get patient details", description = "Returns patient details for the supplied Aadhaar number.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Details returned", content = @Content(schema = @Schema(implementation = UserDetailsDTO.class))), @ApiResponse(responseCode = "404", description = "Account not found")})
    @Parameter(name = "input", in = ParameterIn.PATH, required = true, description = "Account Aadhaar number")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable String input) {
        return new ResponseEntity<>(userService.getUserDetails(input), HttpStatus.OK);
    }

    @GetMapping("/visits/{input}")
    @Operation(summary = "Get patient visits", description = "Returns visits, medicines, allergies, diagnosis information, and doctor details for an account.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Visits returned", content = @Content(schema = @Schema(implementation = VisitResponse.class))), @ApiResponse(responseCode = "404", description = "Account not found")})
    @Parameter(name = "input", in = ParameterIn.PATH, required = true, description = "Account Aadhaar number")
    public ResponseEntity<VisitResponse> getVisits(@PathVariable String input) {
        return new ResponseEntity<>(userService.getVisits(input), HttpStatus.OK);
    }

    @PatchMapping("/visits/{input}/{visitId}/recovery")
    @Operation(summary = "Update patient recovery status", description = "Updates recovery information only for a visit owned by the identified patient.")
    public ResponseEntity<GeneralResponse> updateRecovery(@PathVariable String input,
                                                           @PathVariable Long visitId,
                                                           @RequestBody RecoveryUpdateRequest request) {
        return new ResponseEntity<>(userService.updateRecovery(input, visitId, request), HttpStatus.OK);
    }

    @GetMapping({"/allergy/{input}", "/allergies/{input}"})
    @Operation(summary = "Get patient allergies", description = "Returns allergies associated with an account.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Allergies returned", content = @Content(schema = @Schema(implementation = AllergyResponse.class))), @ApiResponse(responseCode = "404", description = "Account not found")})
    @Parameter(name = "input", in = ParameterIn.PATH, required = true, description = "Account Aadhaar number")
    public ResponseEntity<AllergyResponse> getAllergies(@PathVariable String input) {
        return new ResponseEntity<>(userService.getAllergy(input), HttpStatus.OK);
    }

    @PostMapping("/medicine-safety/{input}")
    @Operation(summary = "Check medicine against patient allergies", description = "Validates a medicines active ingredient or name against the patient's known allergies before prescription creation.")
    public ResponseEntity<MedicineSafetyCheckResponse> checkMedicineSafety(@PathVariable String input, @RequestBody MedicineSafetyRequest request) {
        return new ResponseEntity<>(userService.checkMedicineSafety(input, request), HttpStatus.OK);
    }

    @GetMapping("/history/{input}")
    @Operation(summary = "Get patient history summary", description = "Returns a condensed patient history summary including diagnosis, medicine, allergy, and recovery information.")
    public ResponseEntity<PatientHistorySummaryResponse> getHistorySummary(@PathVariable String input) {
        return new ResponseEntity<>(userService.getHistorySummary(input), HttpStatus.OK);
    }

    @GetMapping("/medicine-feed-back/{medicineName}")
    @Operation(summary = "Get medicine feedback", description = "Returns feedback recorded for prescriptions whose medicine name contains the supplied text.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Feedback result returned", content = @Content(schema = @Schema(implementation = MedicineFeedBackResponse.class)))})
    @Parameter(name = "medicineName", in = ParameterIn.PATH, required = true, description = "Medicine name search text")
    public ResponseEntity<MedicineFeedBackResponse> getMedicineFeedback(@PathVariable String medicineName,
            @RequestHeader(AccountOwnershipInterceptor.ACCOUNT_HEADER) String accountId) {
        return new ResponseEntity<>(userService.getMedicineFeedback(accountId, medicineName), HttpStatus.OK);
    }

    @PostMapping("/medicine-feedback/{input}")
    public ResponseEntity<MedicineFeedbackSubmitResponse> addMedicineFeedback(@PathVariable String input,
                                                                          @RequestBody MedicineFeedbackRequest request) {
        return new ResponseEntity<>(userService.addMedicineFeedback(input, request), HttpStatus.CREATED);
    }


}
