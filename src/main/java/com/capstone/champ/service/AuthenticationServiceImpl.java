package com.capstone.champ.service;

import com.capstone.champ.exception.*;
import com.capstone.champ.model.Role;
import com.capstone.champ.model.User;
import com.capstone.champ.payload.GeneralResponse;
import com.capstone.champ.payload.authentication.*;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.userdetails.UserDetailsDTO;
import com.capstone.champ.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.ast.tree.expression.XmlTableOrdinalityColumnDefinition;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public SignupResponse signup(SignupRequest signupRequest) {
        Optional<User> alreadyExistUser = userRepository.findByAadhaarNumber(signupRequest.getAadhaarNumber());
        if (alreadyExistUser.isPresent())
            throw new AadhaarAlreadyExistsException(signupRequest.getAadhaarNumber());
        User user = new User();
        user.setAadhaarNumber(signupRequest.getAadhaarNumber());
        user.setMobileNumber(signupRequest.getMobileNumber());
        user.setPassword(signupRequest.getPassword());
        user.setVerificationStatus(false);
        userRepository.save(user);
        return new SignupResponse(true, "Signup successful", signupRequest.getAadhaarNumber(), signupRequest.getMobileNumber());
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User alreadyExistsUser = getUser(loginRequest.getInput());
        if (!alreadyExistsUser.getPassword().equals(loginRequest.getPassword()))
            throw new PasswordIncorrectException();
        return new LoginResponse(true, "Login successful", loginRequest.getInput(), alreadyExistsUser.getRole(), alreadyExistsUser.getVerificationStatus());
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
            temp.add(new AadhaarDetailsDTO("********" + user.getAadhaarNumber().substring(8), fullName));
        }
        usernamesDTO.setAadhaarNumbers(temp);
        return usernamesDTO;
    }

    @Override
    public User getUser(String input) {
        if(input.length() != 10 && input.length() != 12)
            throw new InvalidInputException(input);
        return userRepository.findByAadhaarNumber(input)
                    .orElseThrow(() -> new AadhaarNotFoundException(input));
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
                .map(User::getDoctorDetails)
                .map(doctorDetails -> modelMapper.map(doctorDetails, DoctorDetailsDTO.class))
                .toList();
    }

    @Override
    public List<UserDetailsDTO> getPendingUsers() {
        return userRepository.findByRoleAndVerificationStatus(Role.USER.toString(), false)
                .stream()
                .map(User::getUserDetails)
                .map(userDetails -> modelMapper.map(userDetails, UserDetailsDTO.class))
                .toList();
    }


}
