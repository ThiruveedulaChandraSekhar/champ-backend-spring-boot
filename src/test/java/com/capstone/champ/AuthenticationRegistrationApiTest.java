package com.capstone.champ;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.capstone.champ.model.User;
import com.capstone.champ.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationRegistrationApiTest {
        @Autowired private MockMvc mockMvc;
        private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired private UserRepository users;

    @Test
    void patientSignupPersistsLinkedProfileAndCanLogIn() throws Exception {
        String aadhaar = randomDigits(12);
        String mobile = randomDigits(10);
        String password = "patient-password-123";
        Map<String, Object> request = Map.of(
                "aadhaarNumber", aadhaar,
                "mobileNumber", mobile,
                "password", password,
                "confirmPassword", password,
                "role", "PATIENT",
                "patientDetails", Map.of(
                        "fullName", "Patient Integration Test",
                        "gender", "Female",
                        "email", "patient-test@example.invalid",
                        "dateOfBirth", "1995-04-12",
                        "bloodGroup", "O+"
                ),
                "address", Map.of("doorNumber", "1A", "street", "Test Street", "city", "Test City")
        );

        String signupBody = mockMvc.perform(post("/auth/signup").contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode signup = objectMapper.readTree(signupBody);
        assertEquals("USER", signup.path("role").asText());
        assertEquals("Patient Integration Test", signup.path("fullName").asText());

        User saved = users.findByAadhaarNumber(aadhaar).orElseThrow();
        assertNotEquals(password, saved.getPassword());
        assertEquals("Patient Integration Test", saved.getUserDetails().getFullName());
        assertEquals("Test Street", saved.getUserDetails().getAddress().getStreet());

        String loginBody = mockMvc.perform(post("/auth/login").contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("input", aadhaar, "password", password))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode login = objectMapper.readTree(loginBody);
        assertEquals("USER", login.path("role").asText());
        assertEquals(aadhaar, login.path("accountIdentifier").asText());
        assertEquals("Patient Integration Test", login.path("fullName").asText());
    }

    @Test
    void doctorSignupPersistsLinkedProfileAndCanLogIn() throws Exception {
        String aadhaar = randomDigits(12);
        String mobile = randomDigits(10);
        String password = "doctor-password-123";
        Map<String, Object> request = Map.of(
                "aadhaarNumber", aadhaar,
                "mobileNumber", mobile,
                "password", password,
                "confirmPassword", password,
                "role", "DOCTOR",
                "doctorDetails", Map.of(
                        "fullName", "Doctor Integration Test",
                        "registrationNumber", "REG-" + aadhaar,
                        "specialization", "General Medicine",
                        "hospitalName", "Test Clinic",
                        "yearOfRegistration", "2018"
                )
        );

        String signupBody = mockMvc.perform(post("/auth/signup").contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode signup = objectMapper.readTree(signupBody);
        assertEquals("DOCTOR", signup.path("role").asText());
        assertEquals("Doctor Integration Test", signup.path("fullName").asText());

        User saved = users.findByAadhaarNumber(aadhaar).orElseThrow();
        assertNotEquals(password, saved.getPassword());
        assertNotNull(saved.getDoctorDetails());
        assertEquals(saved, saved.getDoctorDetails().getUser());
        assertFalse(saved.getVerificationStatus());

        String loginBody = mockMvc.perform(post("/auth/login").contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("input", aadhaar, "password", password))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode login = objectMapper.readTree(loginBody);
        assertEquals("DOCTOR", login.path("role").asText());
        assertEquals(aadhaar, login.path("accountIdentifier").asText());
        assertEquals("Doctor Integration Test", login.path("fullName").asText());
    }

    private String randomDigits(int length) {
        long minimum = (long) Math.pow(10, length - 1);
        long maximum = (long) Math.pow(10, length) - 1;
        return Long.toString(ThreadLocalRandom.current().nextLong(minimum, maximum + 1));
    }
}