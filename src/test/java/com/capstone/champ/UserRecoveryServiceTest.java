package com.capstone.champ;

import com.capstone.champ.exception.InvalidInputException;
import com.capstone.champ.model.RecoveryStatus;
import com.capstone.champ.model.User;
import com.capstone.champ.model.Visit;
import com.capstone.champ.payload.RecoveryUpdateRequest;
import com.capstone.champ.repository.MedicineRepository;
import com.capstone.champ.repository.PrescriptionRepository;
import com.capstone.champ.repository.UserDetailsRepository;
import com.capstone.champ.repository.UserRepository;
import com.capstone.champ.repository.VisitRepository;
import com.capstone.champ.service.MedicineSafetyService;
import com.capstone.champ.service.PatientHistorySummaryService;
import com.capstone.champ.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserRecoveryServiceTest {
    private UserRepository users;
    private VisitRepository visits;
    private User patient;
    private Visit visit;
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        UserDetailsRepository details = mock(UserDetailsRepository.class);
        users = mock(UserRepository.class);
        visits = mock(VisitRepository.class);
        service = new UserServiceImpl(details, users, mock(ModelMapper.class), mock(PrescriptionRepository.class),
                visits, mock(MedicineRepository.class), mock(MedicineSafetyService.class), mock(PatientHistorySummaryService.class));
        patient = new User(); patient.setId(12L); patient.setAadhaarNumber("patient-id"); patient.setRole("USER");
        visit = new Visit(); visit.setId(34L); visit.setUser(patient); visit.setIssueDate(LocalDate.now().minusDays(4));
        visit.setRecoveryStatus(RecoveryStatus.ONGOING);
        when(users.findByAadhaarNumber("patient-id")).thenReturn(Optional.of(patient));
        when(visits.findById(34L)).thenReturn(Optional.of(visit));
    }

    @Test
    void recoveryUpdatePersistsStatusAndConfirmationFields() {
        RecoveryUpdateRequest request = new RecoveryUpdateRequest();
        request.setRecoveryStatus(RecoveryStatus.RECOVERED);
        request.setOutcomeSource("PATIENT");

        var response = service.updateRecovery("patient-id", 34L, request);

        assertTrue(response.getStatus());
        assertEquals(RecoveryStatus.RECOVERED, visit.getRecoveryStatus());
        assertEquals(LocalDate.now(), visit.getRecoveredDate());
        assertNotNull(visit.getRecoveryConfirmedAt());
        assertEquals("PATIENT", visit.getOutcomeSource());
        verify(visits).save(visit);
    }

    @Test
    void patientCannotUpdateAnotherPatientsVisit() {
        User another = new User(); another.setId(99L); another.setRole("USER");
        visit.setUser(another);
        RecoveryUpdateRequest request = new RecoveryUpdateRequest(); request.setRecoveryStatus(RecoveryStatus.RECOVERED);

        assertThrows(InvalidInputException.class, () -> service.updateRecovery("patient-id", 34L, request));
        verify(visits, never()).save(any(Visit.class));
    }
}
