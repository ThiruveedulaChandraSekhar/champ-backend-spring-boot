package com.capstone.champ.service;

import com.capstone.champ.exception.*;
import com.capstone.champ.model.Address;
import com.capstone.champ.model.DoctorDetails;
import com.capstone.champ.model.Role;
import com.capstone.champ.model.User;
import com.capstone.champ.model.UserDetails;
import com.capstone.champ.payload.GeneralResponse;
import com.capstone.champ.payload.authentication.*;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.userdetails.UserDetailsDTO;
import com.capstone.champ.repository.UserRepository;
import com.capstone.champ.repository.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ModelMapper modelMapper;
    private final PasswordHashingService passwordHashingService;

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        validateSignup(signupRequest);
        Optional<User> alreadyExistUser = userRepository.findByAadhaarNumber(signupRequest.getAadhaarNumber());
        if (alreadyExistUser.isPresent())
            throw new AadhaarAlreadyExistsException(signupRequest.getAadhaarNumber());
        if (!"DOCTOR".equalsIgnoreCase(signupRequest.getRole())
                && signupRequest.getPatientDetails().getEmail() != null
                && !signupRequest.getPatientDetails().getEmail().isBlank()
                && userDetailsRepository.existsByEmailIgnoreCase(signupRequest.getPatientDetails().getEmail().trim()))
            throw new EmailAlreadyExistsException(signupRequest.getPatientDetails().getEmail().trim());
        User user = new User();
        user.setAadhaarNumber(signupRequest.getAadhaarNumber());
        user.setMobileNumber(signupRequest.getMobileNumber());
        user.setPassword(passwordHashingService.encode(signupRequest.getPassword()));
        boolean doctorRegistration = "DOCTOR".equalsIgnoreCase(signupRequest.getRole());
        user.setRole(doctorRegistration ? Role.DOCTOR.toString() : Role.USER.toString());
        user.setVerificationStatus(false);

        String fullName;
        if (doctorRegistration) {
            DoctorDetails details = modelMapper.map(signupRequest.getDoctorDetails(), DoctorDetails.class);
            details.setUser(user);
            user.setDoctorDetails(details);
            fullName = details.getFullName();
        } else {
            UserDetails details = modelMapper.map(signupRequest.getPatientDetails(), UserDetails.class);
            details.setUser(user);
            if (signupRequest.getAddress() != null) {
                Address address = modelMapper.map(signupRequest.getAddress(), Address.class);
                address.setUserDetails(details);
                details.setAddress(address);
            }
            user.setUserDetails(details);
            fullName = details.getFullName();
        }
        userRepository.save(user);
        return new SignupResponse(true, "Signup successful", signupRequest.getAadhaarNumber(),
                signupRequest.getMobileNumber(), user.getRole(), fullName);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getPassword() == null || loginRequest.getPassword().isBlank())
            throw new InvalidInputException("input and password are required");
        User alreadyExistsUser = getUser(loginRequest.getInput());
        if (!passwordHashingService.matches(loginRequest.getPassword(), alreadyExistsUser.getPassword()))
            throw new PasswordIncorrectException();
        if (passwordHashingService.needsRehash(alreadyExistsUser.getPassword())) {
            alreadyExistsUser.setPassword(passwordHashingService.encode(loginRequest.getPassword()));
            userRepository.save(alreadyExistsUser);
        }
        String role = alreadyExistsUser.getRole();
        String fullName = Role.DOCTOR.toString().equals(role)
                ? alreadyExistsUser.getDoctorDetails() == null ? null : alreadyExistsUser.getDoctorDetails().getFullName()
                : alreadyExistsUser.getUserDetails() == null ? null : alreadyExistsUser.getUserDetails().getFullName();
        Long profileId = Role.DOCTOR.toString().equals(role)
                ? alreadyExistsUser.getDoctorDetails() == null ? null : alreadyExistsUser.getDoctorDetails().getId()
                : alreadyExistsUser.getUserDetails() == null ? null : alreadyExistsUser.getUserDetails().getId();
        return new LoginResponse(true, "Login successful", loginRequest.getInput(), role,
                alreadyExistsUser.getVerificationStatus(), alreadyExistsUser.getAadhaarNumber(),
                alreadyExistsUser.getMobileNumber(), fullName, profileId);
    }

    @Override
    public UsernamesDTO getAadhaarDetailsByMobileNumber(String input) {
        if(input.length() != 10)
            throw new InvalidInputException(input);
        List<User> users = userRepository.findByMobileNumber(input);
        if(users.isEmpty())
            throw new MobileNumberNotFoundException(input);
        UsernamesDTO usernamesDTO = new UsernamesDTO();
        usernamesDTO.setStatus(true);
        usernamesDTO.setMessage("Users fetched successfully");
        List<AadhaarDetailsDTO> temp = new ArrayList<>();
        for (User user : users) {
            String fullName = user.getUserDetails() != null ? user.getUserDetails().getFullName() : user.getDoctorDetails() != null ? user.getDoctorDetails().getFullName() : null;
            temp.add(new AadhaarDetailsDTO(user.getAadhaarNumber(), fullName));
        }
        usernamesDTO.setAadhaarNumbers(temp);
        return usernamesDTO;
    }

    @Override
    public User getUser(String input) {
        if(input == null || (input.length() != 10 && input.length() != 12))
            throw new InvalidInputException(input);
        Optional<User> byAadhaar = userRepository.findByAadhaarNumber(input);
        if (byAadhaar.isPresent()) return byAadhaar.get();
        List<User> byMobile = userRepository.findByMobileNumber(input);
        if (byMobile.size() == 1) return byMobile.get(0);
        if (byMobile.size() > 1) throw new InvalidInputException("Mobile number identifies more than one account");
        throw new AadhaarNotFoundException(input);
    }

    @Override
    public DeleteResponse deleteUser(String input) {
        User user = getUser(input);
        userRepository.delete(user);
        return new DeleteResponse(true, "User deleted successfully");
    }

    @Override
    public GeneralResponse verifyUser(String aadhaarNumber) {
        User user = getUser(aadhaarNumber);
        user.setVerificationStatus(true);
        userRepository.save(user);
        return new GeneralResponse(true, "User verified successfully");
    }

    @Override
    public List<DoctorDetailsDTO> getPendingDoctors() {
        return userRepository.findByRoleAndVerificationStatus(Role.DOCTOR.toString(), false)
                .stream()
                .filter(user -> user.getDoctorDetails() != null)
                .map(user -> {
                    DoctorDetailsDTO details = modelMapper.map(user.getDoctorDetails(), DoctorDetailsDTO.class);
                    details.setAccountIdentifier(user.getAadhaarNumber());
                    return details;
                })
                .toList();
    }

    @Override
    public List<UserDetailsDTO> getPendingUsers() {
        return userRepository.findByRoleAndVerificationStatus(Role.USER.toString(), false)
                .stream()
                .filter(user -> user.getUserDetails() != null)
                .map(user -> {
                    UserDetailsDTO details = modelMapper.map(user.getUserDetails(), UserDetailsDTO.class);
                    details.setAccountIdentifier(user.getAadhaarNumber());
                    details.setMobileNumber(user.getMobileNumber());
                    details.setVerificationStatus(user.getVerificationStatus());
                    return details;
                })
                .toList();
    }

    private void validateSignup(SignupRequest request) {
        if (request == null || request.getAadhaarNumber() == null || !request.getAadhaarNumber().matches("\\d{12}"))
            throw new InvalidInputException("Aadhaar number must contain 12 digits");
        if (request.getMobileNumber() == null || !request.getMobileNumber().matches("\\d{10}"))
            throw new InvalidInputException("Mobile number must contain 10 digits");
        if (request.getPassword() == null || request.getPassword().length() < 8)
            throw new InvalidInputException("Password must contain at least 8 characters");
        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new InvalidInputException("Password confirmation does not match");
        if (request.getRole() == null || !("PATIENT".equalsIgnoreCase(request.getRole())
                || "USER".equalsIgnoreCase(request.getRole()) || "DOCTOR".equalsIgnoreCase(request.getRole())))
            throw new InvalidInputException("Role must be PATIENT or DOCTOR");
        if ("DOCTOR".equalsIgnoreCase(request.getRole())) {
            if (request.getDoctorDetails() == null || blank(request.getDoctorDetails().getFullName())
                    || blank(request.getDoctorDetails().getRegistrationNumber()))
                throw new InvalidInputException("Doctor name and registration number are required");
        } else if (request.getPatientDetails() == null || blank(request.getPatientDetails().getFullName())) {
            throw new InvalidInputException("Patient name is required");
        } else if (!blank(request.getPatientDetails().getEmail())
                && !request.getPatientDetails().getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new InvalidInputException("Email address is invalid");
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }


}
