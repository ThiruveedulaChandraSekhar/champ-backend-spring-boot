package com.capstone.champ.controller;

import com.capstone.champ.payload.*;
import com.capstone.champ.service.PatientAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequiredArgsConstructor
public class PatientAccessController {
    private final PatientAccessService service;

    @PostMapping("/doctor/{doctorAccountId}/patient-access/request")
    public ResponseEntity<PatientAccessResponse> request(@PathVariable String doctorAccountId, @RequestBody PatientAccessRequestCreate body) {
        return ResponseEntity.ok(service.request(doctorAccountId, body.getPatientAccountId(), body.getPurpose()));
    }

    @PostMapping("/doctor/{doctorAccountId}/patient-access/verify")
    public ResponseEntity<PatientAccessResponse> verify(@PathVariable String doctorAccountId, @RequestBody PatientAccessOtpVerify body) {
        return ResponseEntity.ok(service.verify(doctorAccountId, body.getRequestId(), body.getPatientAccountId(), body.getOtp()));
    }

    @GetMapping("/patient/{patientAccountId}/access-requests")
    public ResponseEntity<List<PatientAccessRequestDTO>> patientRequests(@PathVariable String patientAccountId) {
        return ResponseEntity.ok(service.patientRequests(patientAccountId));
    }

    @GetMapping("/doctor/{doctorAccountId}/patient-access")
    public ResponseEntity<List<PatientAccessRequestDTO>> doctorRequests(@PathVariable String doctorAccountId, @RequestParam(required = false) String patientAccountId) {
        return ResponseEntity.ok(service.doctorRequests(doctorAccountId, patientAccountId));
    }

    @GetMapping("/patient/{patientAccountId}/notifications")
    public ResponseEntity<List<PatientNotificationDTO>> notifications(@PathVariable String patientAccountId) {
        return ResponseEntity.ok(service.notifications(patientAccountId));
    }

    @PatchMapping("/patient/{patientAccountId}/notifications/{notificationId}/read")
    public ResponseEntity<PatientNotificationDTO> markRead(@PathVariable String patientAccountId, @PathVariable Long notificationId) {
        return ResponseEntity.ok(service.markRead(patientAccountId, notificationId));
    }

    @GetMapping("/patient/{patientAccountId}/settings")
    public ResponseEntity<PatientSettingsDTO> settings(@PathVariable String patientAccountId) {
        return ResponseEntity.ok(service.getSettings(patientAccountId));
    }

    @PatchMapping("/patient/{patientAccountId}/settings")
    public ResponseEntity<PatientSettingsDTO> updateSettings(@PathVariable String patientAccountId, @RequestBody PatientSettingsDTO body) {
        return ResponseEntity.ok(service.updateSettings(patientAccountId, body.isDoctorOtpNotificationsEnabled()));
    }
}
