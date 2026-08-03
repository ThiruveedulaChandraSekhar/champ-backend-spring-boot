package com.capstone.champ.service;

import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.doctordetails.DoctorDetailsRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsResponse;
import com.capstone.champ.repository.DoctorDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService{

    private final DoctorDetailsRepository doctorDetailsRepository;

    @Override
    public DoctorDetailsResponse addDoctorDetails(DoctorDetailsRequest doctorDetailsRequest) {
        return null;
    }

    @Override
    public DoctorDetailsResponse updateDoctorDetails(DoctorDetailsDTO doctorDetailsDTO) {
        return null;
    }
}
