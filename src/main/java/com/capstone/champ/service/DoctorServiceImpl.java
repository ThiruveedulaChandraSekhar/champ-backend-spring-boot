package com.capstone.champ.service;

import com.capstone.champ.exception.DoctorDetailsNotFoundException;
import com.capstone.champ.model.DoctorDetails;
import com.capstone.champ.model.User;
import com.capstone.champ.model.Visit;
import com.capstone.champ.payload.GeneralResponse;
import com.capstone.champ.payload.VisitRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.doctordetails.DoctorDetailsRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsResponse;
import com.capstone.champ.repository.DoctorDetailsRepository;
import com.capstone.champ.repository.UserRepository;
import com.capstone.champ.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService{

    private final DoctorDetailsRepository doctorDetailsRepository;
    private final AuthenticationService authenticationService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;

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

    @Override
    public GeneralResponse addVisit(String doctor, String patient, VisitRequest visitRequest) {

        User doctorUser = authenticationService.getUser(doctor);
        User patientUser = authenticationService.getUser(patient);

        Visit visit = modelMapper.map(visitRequest, Visit.class);
        visit.setDoctorDetails(doctorUser.getDoctorDetails());
        visit.setIssueDate(LocalDate.now());
        visit.setUser(patientUser);
        if (visit.getMedicines() != null) {
            visit.getMedicines().forEach(medicine -> medicine.setVisit(visit));
        }
        if (visit.getAllergies() != null) {
            visit.getAllergies().forEach(allergy -> {
                allergy.setVisit(visit);
                allergy.setUser(patientUser);
                allergy.setDoctorDetails(doctorUser.getDoctorDetails());
            });
        }
        visitRepository.save(visit);

        return new GeneralResponse(true, "Visit added successfully");
    }




}
