package com.capstone.champ.service;

import com.capstone.champ.payload.AllergyResponse;
import com.capstone.champ.payload.MedicineFeedBackResponse;
import com.capstone.champ.payload.VisitResponse;
import com.capstone.champ.payload.userdetails.UserDetailsDTO;
import com.capstone.champ.payload.userdetails.UserDetailsRequest;
import com.capstone.champ.payload.userdetails.UserDetailsResponse;

public interface UserService {
    UserDetailsResponse addUserDetails(String aadhaarNumber, UserDetailsRequest userDetailsRequest);
    UserDetailsResponse updateUserDetails(UserDetailsDTO userDetailsDTO);
    UserDetailsDTO getUserDetails(String aadhaarNumber);
    VisitResponse getVisits(String input);
    AllergyResponse getAllergy(String input);

    MedicineFeedBackResponse getMedicineFeedback(String medicineName);
}
