package com.capstone.champ.service;

import com.capstone.champ.exception.DoctorDetailsNotFoundException;
import com.capstone.champ.model.DoctorDetails;
import com.capstone.champ.model.User;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.doctordetails.DoctorDetailsRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsResponse;
import com.capstone.champ.repository.DoctorDetailsRepository;
import com.capstone.champ.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService{

    private final DoctorDetailsRepository doctorDetailsRepository;
    private final AuthenticationService authenticationService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public DoctorDetailsResponse addDoctorDetails(DoctorDetailsRequest doctorDetailsRequest, String input) {
        User doctor = authenticationService.getUser(input);
        DoctorDetails doctorDetails = modelMapper.map(doctorDetailsRequest, DoctorDetails.class);
        doctorDetails.setUser(doctor);
        doctor.setDoctorDetails(doctorDetails);
        userRepository.save(doctor);
        return new DoctorDetailsResponse(true, "Added details of the doctor");
    }

    @Override
    public DoctorDetailsResponse updateDoctorDetails(DoctorDetailsDTO doctorDetailsDTO) {

        DoctorDetails doctorDetails = doctorDetailsRepository.findById(doctorDetailsDTO.getId())
                .orElseThrow(DoctorDetailsNotFoundException::new);
        modelMapper.map(doctorDetailsDTO, doctorDetails);
        doctorDetailsRepository.save(doctorDetails);
        return new DoctorDetailsResponse(true, "Doctor details updated successfully");
    }

    @Override
    public DoctorDetailsDTO getDoctorDetails(String input) {
        User doctor = authenticationService.getUser(input);
        if (doctor.getDoctorDetails() == null)
            throw new DoctorDetailsNotFoundException();
        return modelMapper.map(doctor.getDoctorDetails(), DoctorDetailsDTO.class);
    }


}
