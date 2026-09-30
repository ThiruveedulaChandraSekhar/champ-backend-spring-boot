package com.capstone.champ.service;

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

public interface UserService {
    UserDetailsResponse addUserDetails(String aadhaarNumber, UserDetailsRequest userDetailsRequest);
    UserDetailsResponse updateUserDetails(UserDetailsDTO userDetailsDTO);
    UserDetailsDTO getUserDetails(String aadhaarNumber);
    VisitResponse getVisits(String input);
    VisitResponse getDoctorOwnVisits(String doctorInput, String patientInput);
    GeneralResponse updateRecovery(String input, Long visitId, RecoveryUpdateRequest request);
    AllergyResponse getAllergy(String input);
    MedicineSafetyCheckResponse checkMedicineSafety(String input, MedicineSafetyRequest request);
    PatientHistorySummaryResponse getHistorySummary(String input);

    MedicineFeedBackResponse getMedicineFeedback(String aadhaarNumber, String medicineName);
    MedicineFeedbackSubmitResponse addMedicineFeedback(String aadhaarNumber, MedicineFeedbackRequest request);
}
