package com.capstone.champ;

import com.capstone.champ.exception.InvalidInputException;
import com.capstone.champ.exception.EmailAlreadyExistsException;
import com.capstone.champ.model.DoctorDetails;
import com.capstone.champ.model.User;
import com.capstone.champ.model.UserDetails;
import com.capstone.champ.payload.authentication.LoginRequest;
import com.capstone.champ.payload.authentication.LoginResponse;
import com.capstone.champ.payload.authentication.SignupRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsRequest;
import com.capstone.champ.payload.userdetails.AddressRequest;
import com.capstone.champ.payload.userdetails.UserDetailsRequest;
import com.capstone.champ.repository.UserRepository;
import com.capstone.champ.repository.UserDetailsRepository;
import com.capstone.champ.service.AuthenticationServiceImpl;
import com.capstone.champ.service.PasswordHashingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceSignupTest {
    private UserRepository users;
    private UserDetailsRepository userDetails;
    private PasswordHashingService passwords;
    private AuthenticationServiceImpl service;

    @BeforeEach
    void setUp() {
        users = mock(UserRepository.class);
        userDetails = mock(UserDetailsRepository.class);
        passwords = new PasswordHashingService();
        service = new AuthenticationServiceImpl(users, userDetails, new ModelMapper(), passwords);
        when(users.findByAadhaarNumber(any())).thenReturn(Optional.empty());
    }

    @Test
    void patientSignupStoresHashedCredentialsAndLinksPatientAndAddress() {
        SignupRequest request = baseRequest("PATIENT");
        UserDetailsRequest details = new UserDetailsRequest();
        details.setFullName("Taylor Patient");
        details.setGender("Female");
        details.setEmail("taylor@example.test");
        request.setPatientDetails(details);
        request.setAddress(new AddressRequest("10001", "12A", "Main Street", "Example City", "Example State"));

        var response = service.signup(request);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(users).save(saved.capture());
        User user = saved.getValue();
        assertTrue(response.getStatus());
        assertEquals("USER", user.getRole());
        assertFalse(user.getVerificationStatus());
        assertNotEquals(request.getPassword(), user.getPassword());
        assertTrue(passwords.matches(request.getPassword(), user.getPassword()));
        assertSame(user, user.getUserDetails().getUser());
        assertEquals("Taylor Patient", user.getUserDetails().getFullName());
        assertSame(user.getUserDetails(), user.getUserDetails().getAddress().getUserDetails());
        assertEquals("Main Street", user.getUserDetails().getAddress().getStreet());
    }

    @Test
    void doctorSignupStoresDoctorRoleAndLeavesApprovalPending() {
        SignupRequest request = baseRequest("DOCTOR");
        DoctorDetailsRequest details = new DoctorDetailsRequest();
        details.setFullName("Morgan Doctor");
        details.setHospitalName("City Clinic");
        details.setSpecialization("General Medicine");
        details.setRegistrationNumber("MED-12345");
        details.setYearOfRegistration("2018");
        request.setDoctorDetails(details);

        service.signup(request);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(users).save(saved.capture());
        User user = saved.getValue();
        DoctorDetails doctor = user.getDoctorDetails();
        assertEquals("DOCTOR", user.getRole());
        assertFalse(user.getVerificationStatus());
        assertSame(user, doctor.getUser());
        assertEquals("MED-12345", doctor.getRegistrationNumber());
    }

    @Test
    void signupRejectsPasswordConfirmationMismatchBeforeSaving() {
        SignupRequest request = baseRequest("PATIENT");
        request.setConfirmPassword("different-pass");
        UserDetailsRequest details = new UserDetailsRequest();
        details.setFullName("Taylor Patient");
        request.setPatientDetails(details);

        assertThrows(InvalidInputException.class, () -> service.signup(request));
        verify(users, never()).save(any(User.class));
    }

    @Test
    void patientSignupRejectsEmailAlreadyInUse() {
        SignupRequest request = baseRequest("PATIENT");
        UserDetailsRequest details = new UserDetailsRequest();
        details.setFullName("Taylor Patient");
        details.setEmail("existing@example.test");
        request.setPatientDetails(details);
        when(userDetails.existsByEmailIgnoreCase("existing@example.test")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> service.signup(request));
        verify(users, never()).save(any(User.class));
    }

    @Test
    void successfulLoginUpgradesLegacyPlaintextAndReturnsProfileIdentity() {
        User user = new User();
        user.setAadhaarNumber("123456789012");
        user.setMobileNumber("9876543210");
        user.setRole("USER");
        user.setVerificationStatus(false);
        user.setPassword("legacy-pass");
        UserDetails details = new UserDetails();
        details.setId(9L);
        details.setFullName("Taylor Patient");
        user.setUserDetails(details);
        when(users.findByAadhaarNumber("123456789012")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setInput("123456789012");
        request.setPassword("legacy-pass");

        LoginResponse response = service.login(request);

        assertEquals("USER", response.getRole());
        assertEquals("Taylor Patient", response.getFullName());
        assertEquals(9L, response.getProfileId());
        assertEquals("123456789012", response.getAccountIdentifier());
        assertTrue(passwords.matches("legacy-pass", user.getPassword()));
        assertNotEquals("legacy-pass", user.getPassword());
        verify(users).save(user);
    }

    private SignupRequest baseRequest(String role) {
        SignupRequest request = new SignupRequest();
        request.setAadhaarNumber("123456789012");
        request.setMobileNumber("9876543210");
        request.setPassword("secure-pass-123");
        request.setConfirmPassword("secure-pass-123");
        request.setRole(role);
        return request;
    }
}