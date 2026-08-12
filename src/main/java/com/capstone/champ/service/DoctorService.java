package com.capstone.champ.service;

import com.capstone.champ.payload.GeneralResponse;
import com.capstone.champ.payload.VisitRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.doctordetails.DoctorDetailsRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsResponse;

public interface DoctorService {
    DoctorDetailsResponse addDoctorDetails(DoctorDetailsRequest doctorDetailsRequest, String input);
    DoctorDetailsResponse updateDoctorDetails(DoctorDetailsDTO doctorDetailsDTO);
    DoctorDetailsDTO getDoctorDetails(String input);
    GeneralResponse addVisit(String doctor, String patient, VisitRequest visitRequest);
}
