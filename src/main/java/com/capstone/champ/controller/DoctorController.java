package com.capstone.champ.controller;

import com.capstone.champ.payload.PatientHistorySummaryResponse;
import com.capstone.champ.payload.VisitRequest;
import com.capstone.champ.payload.GeneralResponse;
import com.capstone.champ.payload.AllergyResponse;
import com.capstone.champ.payload.VisitResponse;
import com.capstone.champ.payload.PatientDirectoryDTO;
import com.capstone.champ.payload.userdetails.UserDetailsDTO;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.doctordetails.DoctorDetailsRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsResponse;
import com.capstone.champ.service.DoctorService;
import com.capstone.champ.service.PatientAccessService;
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
@RequestMapping("/doctor")
@RequiredArgsConstructor
@Tag(name = "Doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final PatientAccessService patientAccessService;
    private final UserService userService;

    @PostMapping("/details/{input}")
    @Operation(summary = "Create doctor details", description = "Adds professional details to the account identified by the path value.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Details created", content = @Content(schema = @Schema(implementation = DoctorDetailsResponse.class))), @ApiResponse(responseCode = "404", description = "Account not found")})
    @Parameter(name = "input", in = ParameterIn.PATH, required = true, description = "Doctor Aadhaar number")
    public ResponseEntity<DoctorDetailsResponse> addDoctorDetails(@RequestBody DoctorDetailsRequest doctorDetailsRequest, @PathVariable String input) {
        return new ResponseEntity<>(doctorService.addDoctorDetails(doctorDetailsRequest, input), HttpStatus.CREATED);
    }

    @PutMapping("/details")
    @Operation(summary = "Update doctor details", description = "Updates stored professional details identified by the request DTO id.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Details updated", content = @Content(schema = @Schema(implementation = DoctorDetailsResponse.class))), @ApiResponse(responseCode = "404", description = "Details not found")})
    public ResponseEntity<DoctorDetailsResponse> updateDoctorDetails(@RequestBody DoctorDetailsDTO doctorDetailsDTO,
            @RequestHeader(AccountOwnershipInterceptor.ACCOUNT_HEADER) String accountId) {
        if (doctorDetailsDTO.getAccountIdentifier() == null || !accountId.equals(doctorDetailsDTO.getAccountIdentifier()))
            throw new com.capstone.champ.exception.InvalidInputException("Doctor account does not own this profile");
        return new ResponseEntity<>(doctorService.updateDoctorDetails(doctorDetailsDTO), HttpStatus.OK);
    }

    @GetMapping("/details/{input}")
    @Operation(summary = "Get doctor details", description = "Returns professional details for the supplied account identifier.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Details returned", content = @Content(schema = @Schema(implementation = DoctorDetailsDTO.class))), @ApiResponse(responseCode = "404", description = "Doctor details not found")})
    @Parameter(name = "input", in = ParameterIn.PATH, required = true, description = "Doctor Aadhaar number or mobile number")
    public ResponseEntity<DoctorDetailsDTO> getDoctorDetails(@PathVariable String input) {
        return new ResponseEntity<>(doctorService.getDoctorDetails(input), HttpStatus.OK);
    }

    @PostMapping("/visit/{doctor}/{patient}")
    @Operation(summary = "Record a patient visit", description = "Creates a visit for a patient and associates the supplied diagnosis, medicines, and allergies when present.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Visit created", content = @Content(schema = @Schema(implementation = GeneralResponse.class))), @ApiResponse(responseCode = "400", description = "Invalid diagnosis, medicine, or recovery input"), @ApiResponse(responseCode = "404", description = "Doctor or patient account not found")})
    @Parameter(name = "doctor", in = ParameterIn.PATH, required = true, description = "Doctor Aadhaar number or mobile number")
    @Parameter(name = "patient", in = ParameterIn.PATH, required = true, description = "Patient Aadhaar number or mobile number")
    public ResponseEntity<?> addVisit(@PathVariable String doctor, @PathVariable String patient, @RequestBody VisitRequest visitRequest) {
        return new ResponseEntity<>(doctorService.addVisit(doctor, patient, visitRequest), HttpStatus.CREATED);
    }

    @GetMapping("/history/{doctor}/{patient}")
    @Operation(summary = "Get patient history summary", description = "Returns a compact visit, diagnosis, medicine, allergy and recovery summary for the nominated patient.")
    public ResponseEntity<PatientHistorySummaryResponse> getPatientHistorySummary(@PathVariable String doctor, @PathVariable String patient) {
        return new ResponseEntity<>(doctorService.getPatientHistorySummary(doctor, patient), HttpStatus.OK);
    }

    @GetMapping("/{doctor}/patients")
    public ResponseEntity<java.util.List<PatientDirectoryDTO>> searchPatients(@PathVariable String doctor, @RequestParam(required = false) String query) {
        return ResponseEntity.ok(doctorService.searchPatients(doctor, query));
    }

    @GetMapping("/{doctor}/patient/{patient}/details")
    public ResponseEntity<UserDetailsDTO> getAuthorizedPatientDetails(@PathVariable String doctor, @PathVariable String patient) {
        requireVerifiedAccess(doctor, patient);
        return ResponseEntity.ok(userService.getUserDetails(patient));
    }

    @GetMapping("/{doctor}/patient/{patient}/visits")
    public ResponseEntity<VisitResponse> getAuthorizedPatientVisits(@PathVariable String doctor, @PathVariable String patient) {
        requireVerifiedAccess(doctor, patient);
        return ResponseEntity.ok(userService.getVisits(patient));
    }

    @GetMapping("/{doctor}/patient/{patient}/allergies")
    public ResponseEntity<AllergyResponse> getAuthorizedPatientAllergies(@PathVariable String doctor, @PathVariable String patient) {
        requireVerifiedAccess(doctor, patient);
        return ResponseEntity.ok(userService.getAllergy(patient));
    }

    private void requireVerifiedAccess(String doctor, String patient) {
        if (!patientAccessService.hasVerifiedAccess(doctor, patient))
            throw new com.capstone.champ.exception.InvalidInputException("Verified patient access is required");
    }

}
