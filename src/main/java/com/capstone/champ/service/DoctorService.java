package com.capstone.champ.service;

import com.capstone.champ.payload.GeneralResponse;
import com.capstone.champ.payload.PatientHistorySummaryResponse;
import com.capstone.champ.payload.VisitRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.doctordetails.DoctorDetailsRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsResponse;
import com.capstone.champ.payload.PatientDirectoryDTO;
import com.capstone.champ.payload.DoctorMedicineFeedbackDTO;
import java.util.List;

public interface DoctorService {
    DoctorDetailsResponse addDoctorDetails(DoctorDetailsRequest doctorDetailsRequest, String input);
    DoctorDetailsResponse updateDoctorDetails(DoctorDetailsDTO doctorDetailsDTO);
    DoctorDetailsDTO getDoctorDetails(String input);
    GeneralResponse addVisit(String doctor, String patient, VisitRequest visitRequest);
    PatientHistorySummaryResponse getPatientHistorySummary(String doctorInput, String patientInput);
    List<PatientDirectoryDTO> searchPatients(String doctorInput, String query);
    List<PatientDirectoryDTO> getDoctorPreviousPatients(String doctorInput);
    List<DoctorMedicineFeedbackDTO> getPatientMedicineFeedback(String doctorInput);
}
