package com.capstone.champ;

import com.capstone.champ.model.DoctorDetails;
import com.capstone.champ.model.User;
import com.capstone.champ.model.UserDetails;
import com.capstone.champ.payload.PatientDirectoryDTO;
import com.capstone.champ.repository.DiagnosisRepository;
import com.capstone.champ.repository.DoctorDetailsRepository;
import com.capstone.champ.repository.MedicineRepository;
import com.capstone.champ.repository.PrescriptionRepository;
import com.capstone.champ.repository.UserRepository;
import com.capstone.champ.repository.VisitRepository;
import com.capstone.champ.service.AuthenticationService;
import com.capstone.champ.service.DoctorServiceImpl;
import com.capstone.champ.service.MedicineSafetyService;
import com.capstone.champ.service.MlPredictionService;
import com.capstone.champ.service.PatientAccessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DoctorPatientDirectoryServiceTest {
    private UserRepository users;
    private AuthenticationService authentication;
    private DoctorServiceImpl service;

    @BeforeEach
    void setUp() {
        users = mock(UserRepository.class);
        authentication = mock(AuthenticationService.class);
        VisitRepository visits = mock(VisitRepository.class);
        service = new DoctorServiceImpl(mock(DoctorDetailsRepository.class), authentication, mock(ModelMapper.class), users,
                visits, mock(DiagnosisRepository.class), mock(MedicineRepository.class), mock(PrescriptionRepository.class),
                mock(MedicineSafetyService.class), mock(MlPredictionService.class), mock(com.capstone.champ.service.PatientHistorySummaryService.class),
                mock(PatientAccessService.class));

        User doctor = new User();
        doctor.setRole("DOCTOR");
        doctor.setDoctorDetails(new DoctorDetails());
        when(authentication.getUser("doctor-aadhaar")).thenReturn(doctor);
    }

    @Test
    void searchesOnlyTheExactAadhaarAndNeverLoadsAllUsers() {
        User patient = new User();
        patient.setRole("USER");
        patient.setAadhaarNumber("123456789012");
        UserDetails details = new UserDetails();
        details.setFullName("Patient A");
        patient.setUserDetails(details);
        when(users.findByAadhaarNumberAndRole("123456789012", "USER")).thenReturn(Optional.of(patient));

        var result = service.searchPatients("doctor-aadhaar", "123456789012");

        assertEquals(1, result.size());
        assertEquals("123456789012", result.getFirst().getAccountIdentifier());
        verify(users).findByAadhaarNumberAndRole("123456789012", "USER");
        verify(users, never()).findAll();
    }

    @Test
    void emptySearchReturnsNoDirectoryResults() {
        var result = service.searchPatients("doctor-aadhaar", " ");

        assertEquals(0, result.size());
        verify(users, never()).findAll();
        verify(users, never()).findByAadhaarNumberAndRole(anyString(), anyString());
    }
}