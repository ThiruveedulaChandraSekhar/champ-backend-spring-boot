package com.capstone.champ;

import com.capstone.champ.exception.InvalidInputException;
import com.capstone.champ.model.DoctorPatientAccessRequest;
import com.capstone.champ.model.PatientNotification;
import com.capstone.champ.model.User;
import com.capstone.champ.repository.DoctorPatientAccessRequestRepository;
import com.capstone.champ.repository.PatientNotificationRepository;
import com.capstone.champ.repository.UserRepository;
import com.capstone.champ.service.PatientAccessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientAccessServiceTest {
    private UserRepository users;
    private DoctorPatientAccessRequestRepository requests;
    private PatientNotificationRepository notifications;
    private PatientAccessService service;
    private User doctor;
    private User patient;

    @BeforeEach
    void setup() {
        users = mock(UserRepository.class);
        requests = mock(DoctorPatientAccessRequestRepository.class);
        notifications = mock(PatientNotificationRepository.class);
        service = new PatientAccessService(users, requests, notifications);
        ReflectionTestUtils.setField(service, "otpExpirationMinutes", 10L);
        doctor = new User(); doctor.setId(1L); doctor.setAadhaarNumber("doctor-id"); doctor.setRole("DOCTOR");
        patient = new User(); patient.setId(2L); patient.setAadhaarNumber("patient-id"); patient.setRole("USER");
        patient.setDoctorOtpNotificationsEnabled(true);
        when(users.findByAadhaarNumber("doctor-id")).thenReturn(Optional.of(doctor));
        when(users.findByAadhaarNumber("patient-id")).thenReturn(Optional.of(patient));
        when(requests.save(any(DoctorPatientAccessRequest.class))).thenAnswer(invocation -> {
            DoctorPatientAccessRequest row = invocation.getArgument(0);
            if (row.getId() == null) row.setId(77L);
            return row;
        });
    }

    @Test
    void requestCreatesSecureOtpAndPersistentNotificationWithoutReturningOtp() {
        var response = service.request("doctor-id", "patient-id", "consultation");
        var request = org.mockito.ArgumentCaptor.forClass(DoctorPatientAccessRequest.class);
        verify(requests).save(request.capture());
        assertEquals(77L, response.getRequestId());
        assertEquals("PENDING", response.getStatus());
        assertTrue(request.getValue().getOtp().matches("\\d{6}"));
        assertNotEquals("123456", request.getValue().getOtp());
        assertEquals(doctor, request.getValue().getDoctor());
        assertEquals(patient, request.getValue().getPatient());
        assertEquals(request.getValue().getCreatedAt().plusMinutes(10), request.getValue().getExpiresAt());
        var notification = org.mockito.ArgumentCaptor.forClass(PatientNotification.class);
        verify(notifications).save(notification.capture());
        assertEquals(patient, notification.getValue().getPatient());
        assertEquals(request.getValue().getOtp(), notification.getValue().getOtp());
    }

    @Test
    void successfulOtpIsSingleUseAndBoundToBothAccounts() {
        DoctorPatientAccessRequest row = pendingRequest("817204");
        when(requests.findById(77L)).thenReturn(Optional.of(row));
        when(requests.findFirstByDoctorIdAndPatientIdAndStatusOrderByCreatedAtDesc(1L, 2L, "VERIFIED"))
                .thenReturn(Optional.of(row));
        var verified = service.verify("doctor-id", 77L, "patient-id", "817204");
        assertEquals("VERIFIED", verified.getStatus());
        assertNotNull(row.getUsedAt());
        assertThrows(InvalidInputException.class, () -> service.verify("doctor-id", 77L, "patient-id", "817204"));
        assertTrue(service.hasVerifiedAccess("doctor-id", "patient-id"));
    }

    @Test
    void invalidOtpIsRejected() {
        when(requests.findById(77L)).thenReturn(Optional.of(pendingRequest("817204")));
        assertTrue(assertThrows(InvalidInputException.class,
                () -> service.verify("doctor-id", 77L, "patient-id", "111111")).getMessage().contains("Invalid OTP"));
    }

    @Test
    void expiredOtpIsRejected() {
        DoctorPatientAccessRequest row = pendingRequest("817204");
        row.setExpiresAt(LocalDateTime.now().minusSeconds(1));
        when(requests.findById(77L)).thenReturn(Optional.of(row));
        assertTrue(assertThrows(InvalidInputException.class,
                () -> service.verify("doctor-id", 77L, "patient-id", "817204")).getMessage().contains("expired"));
    }

    @Test
    void requestCannotBeVerifiedByWrongDoctorOrPatient() {
        when(requests.findById(77L)).thenReturn(Optional.of(pendingRequest("817204")));
        User otherDoctor = new User(); otherDoctor.setId(3L); otherDoctor.setAadhaarNumber("other-doctor"); otherDoctor.setRole("DOCTOR");
        User otherPatient = new User(); otherPatient.setId(4L); otherPatient.setAadhaarNumber("other-patient"); otherPatient.setRole("USER");
        when(users.findByAadhaarNumber("other-doctor")).thenReturn(Optional.of(otherDoctor));
        when(users.findByAadhaarNumber("other-patient")).thenReturn(Optional.of(otherPatient));
        assertThrows(InvalidInputException.class, () -> service.verify("other-doctor", 77L, "patient-id", "817204"));
        assertThrows(InvalidInputException.class, () -> service.verify("doctor-id", 77L, "other-patient", "817204"));
    }

    @Test
    void settingsDefaultEnabledAndPersistChanges() {
        assertTrue(service.getSettings("patient-id").isDoctorOtpNotificationsEnabled());
        assertFalse(service.updateSettings("patient-id", false).isDoctorOtpNotificationsEnabled());
        verify(users).save(patient);
        assertFalse(service.getSettings("patient-id").isDoctorOtpNotificationsEnabled());
    }

    @Test
    void disabledPreferenceDoesNotCreateNotificationOrGrantAccess() {
        patient.setDoctorOtpNotificationsEnabled(false);
        service.request("doctor-id", "patient-id", "consultation");
        verify(notifications, never()).save(any(PatientNotification.class));
        verify(requests).save(any(DoctorPatientAccessRequest.class));
        assertFalse(service.hasVerifiedAccess("doctor-id", "patient-id"));
    }

    @Test
    void patientNotificationRetrievalIsScopedAndContainsPendingOtp() {
        PatientNotification notification = new PatientNotification();
        notification.setId(90L); notification.setPatient(patient); notification.setAccessRequestId(77L);
        notification.setCreatedAt(LocalDateTime.now()); notification.setOtp("817204"); notification.setRead(false);
        when(notifications.findByPatientIdOrderByCreatedAtDesc(2L)).thenReturn(List.of(notification));
        when(requests.findById(77L)).thenReturn(Optional.of(pendingRequest("817204")));
        User otherPatient = new User(); otherPatient.setId(4L); otherPatient.setAadhaarNumber("other-patient"); otherPatient.setRole("USER");
        when(users.findByAadhaarNumber("other-patient")).thenReturn(Optional.of(otherPatient));

        var result = service.notifications("patient-id");
        assertEquals(1, result.size());
        assertEquals("817204", result.getFirst().getOtp());
        verify(notifications).findByPatientIdOrderByCreatedAtDesc(2L);
        assertThrows(InvalidInputException.class, () -> service.markRead("other-patient", 90L));
    }

    private DoctorPatientAccessRequest pendingRequest(String otp) {
        DoctorPatientAccessRequest row = new DoctorPatientAccessRequest();
        row.setId(77L); row.setDoctor(doctor); row.setPatient(patient); row.setOtp(otp);
        row.setStatus("PENDING"); row.setCreatedAt(LocalDateTime.now()); row.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        return row;
    }
}
