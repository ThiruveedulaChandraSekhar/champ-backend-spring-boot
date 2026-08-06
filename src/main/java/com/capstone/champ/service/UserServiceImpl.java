package com.capstone.champ.service;

import com.capstone.champ.exception.AadhaarNotFoundException;
import com.capstone.champ.exception.UserDetailsNotFoundException;
import com.capstone.champ.model.*;
import com.capstone.champ.payload.*;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.userdetails.UserDetailsDTO;
import com.capstone.champ.payload.userdetails.UserDetailsRequest;
import com.capstone.champ.payload.userdetails.UserDetailsResponse;
import com.capstone.champ.repository.UserDetailsRepository;
import com.capstone.champ.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserDetailsRepository userDetailsRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDetailsResponse addUserDetails(String aadhaarNumber, UserDetailsRequest userDetailsRequest) {
        User user = userRepository.findByAadhaarNumber(aadhaarNumber)
                .orElseThrow(() -> new AadhaarNotFoundException(aadhaarNumber));
        UserDetails userDetails = modelMapper.map(userDetailsRequest, UserDetails.class);
        userDetails.setCreated(LocalDateTime.now());
        userDetails.setLastUpdated(LocalDateTime.now());
        userDetails.setUser(user);
        user.setUserDetails(userDetails);
        userRepository.save(user);
        return new UserDetailsResponse(true, "User details added successfully");
    }

    @Override
    public UserDetailsResponse updateUserDetails(UserDetailsDTO dto) {

        UserDetails userDetails = userDetailsRepository.findById(dto.getId())
                .orElseThrow(() -> new UserDetailsNotFoundException());

        userDetails.setFullName(dto.getFullName());
        userDetails.setGender(dto.getGender());
        userDetails.setEmergencyContact(dto.getEmergencyContact());
        userDetails.setEmail(dto.getEmail());
        userDetails.setGuardian(dto.getGuardian());
        userDetails.setGuardianContact(dto.getGuardianContact());
        userDetails.setDateOfBirth(dto.getDateOfBirth());
        userDetails.setBloodGroup(dto.getBloodGroup());

        userDetails.setLastUpdated(LocalDateTime.now());

        userDetailsRepository.save(userDetails);

        return new UserDetailsResponse(true, "User details updated successfully");
    }

    @Override
    public UserDetailsDTO getUserDetails(String aadhaarNumber) {
        User user = userRepository.findByAadhaarNumber(aadhaarNumber)
                .orElseThrow(() -> new AadhaarNotFoundException(aadhaarNumber));
        UserDetails userDetails = user.getUserDetails();
        return modelMapper.map(userDetails, UserDetailsDTO.class);
    }

    @Override
    public VisitResponse getVisits(String input) {
        User user = userRepository.findByAadhaarNumber(input)
                .orElseThrow(() -> new AadhaarNotFoundException(input));
        List<Visit> visits = user.getVisits();
        List<VisitDTO> visitsDTO = new ArrayList<>();
        for(Visit visit : visits) {
            VisitDTO temp = modelMapper.map(visit, VisitDTO.class);
            modelMapper.map(temp, visit);
            List<MedicineDTO> medicineDTO = new ArrayList<>();
            for(Medicine medicine : visit.getMedicines())
                medicineDTO.add(modelMapper.map(medicine, MedicineDTO.class));
            temp.setMedicines(medicineDTO);
            List<AllergyDTO> allergyDTOS = new ArrayList<>();
            for(Allergy allergy : visit.getAllergies())
                allergyDTOS.add(modelMapper.map(allergy, AllergyDTO.class));
            temp.setAllergies(allergyDTOS);
            temp.setDoctorDetails(modelMapper.map(visit.getDoctorDetails(), DoctorDetailsDTO.class));
            visitsDTO.add(temp);
        }
        return new VisitResponse(true, "Successfully got visit details", visitsDTO);
    }

    @Override
    public AllergyResponse getAllergy(String input) {
        User user = userRepository.findByAadhaarNumber(input)
                .orElseThrow(() -> new AadhaarNotFoundException(input));
        List<Allergy> allergies = user.getAllergies();
        List<AllergyDTO> allergyDTOS = new ArrayList<>();
        for(Allergy allergy : allergies) {
            AllergyDTO allergyDTO = modelMapper.map(allergy, AllergyDTO.class);
            allergyDTO.setDoctorDetails(modelMapper.map(allergy.getDoctorDetails(), DoctorDetailsDTO.class));
            allergyDTOS.add(allergyDTO);
        }
        return new AllergyResponse(true, "Successfully got allergy details", allergyDTOS);
    }
}
