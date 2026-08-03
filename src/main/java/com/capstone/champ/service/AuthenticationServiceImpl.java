package com.capstone.champ.service;

import com.capstone.champ.exception.*;
import com.capstone.champ.model.Role;
import com.capstone.champ.model.User;
import com.capstone.champ.payload.authentication.LoginRequest;
import com.capstone.champ.payload.authentication.LoginResponse;
import com.capstone.champ.payload.authentication.SignupRequest;
import com.capstone.champ.payload.authentication.SignupResponse;
import com.capstone.champ.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{

    private final UserRepository userRepository;

    @Override
    public SignupResponse signup(SignupRequest signupRequest) {
        Optional<User> alreadyExistUser = userRepository.findByAadhaarNumber(signupRequest.getAadhaarNumber());
        if (alreadyExistUser.isPresent())
            throw new AadhaarAlreadyExistsException(signupRequest.getAadhaarNumber());
        User user = new User();
        user.setAadhaarNumber(signupRequest.getAadhaarNumber());
        user.setMobileNumber(signupRequest.getMobileNumber());
        user.setPassword(signupRequest.getPassword());
        user.setRole(Role.USER.toString());
        userRepository.save(user);
        return new SignupResponse(true, "Signup successful", signupRequest.getAadhaarNumber(), signupRequest.getMobileNumber());
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        if(loginRequest.getInput().length() != 10 && loginRequest.getInput().length() != 12)
            throw new InvalidInputException(loginRequest.getInput());
        User alreadyExistsUser;
        if(loginRequest.getInput().length() == 10) {
            alreadyExistsUser = userRepository.findByMobileNumber(loginRequest.getInput())
                    .orElseThrow(() -> new MobileNumberNotFoundException(loginRequest.getInput()));
        } else {
            alreadyExistsUser = userRepository.findByAadhaarNumber(loginRequest.getInput())
                    .orElseThrow(() -> new AadhaarNotFoundException(loginRequest.getInput()));
        }
        if (!alreadyExistsUser.getPassword().equals(loginRequest.getPassword()))
            throw new PasswordIncorrectException();
        return new LoginResponse(true, "Login successful", loginRequest.getInput(), alreadyExistsUser.getRole());
    }
}
