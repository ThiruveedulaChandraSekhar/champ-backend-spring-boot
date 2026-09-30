package com.capstone.champ.service;

import com.capstone.champ.exception.AadhaarNotFoundException;
import com.capstone.champ.exception.InvalidInputException;
import com.capstone.champ.model.DoctorPatientAccessRequest;
import com.capstone.champ.model.PatientNotification;
import com.capstone.champ.model.User;
import com.capstone.champ.payload.PatientAccessResponse;
import com.capstone.champ.payload.PatientNotificationDTO;
import com.capstone.champ.payload.PatientSettingsDTO;
import com.capstone.champ.payload.PatientAccessRequestDTO;
import com.capstone.champ.repository.DoctorPatientAccessRequestRepository;
import com.capstone.champ.repository.PatientNotificationRepository;
import com.capstone.champ.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class PatientAccessService {
    private final UserRepository users;
    private final DoctorPatientAccessRequestRepository requests;
    private final PatientNotificationRepository notifications;
    private final SecureRandom random = new SecureRandom();
    @Value("${champ.access.otp-expiration-minutes:10}") private long otpExpirationMinutes;

    @Transactional
    public PatientAccessResponse request(String doctorInput, String patientInput, String purpose) {
        User doctor = requireRole(doctorInput, "DOCTOR");
        User patient = requireRole(patientInput, "USER");
        LocalDateTime now = LocalDateTime.now();
        requests.findByDoctorIdAndPatientIdAndStatus(doctor.getId(), patient.getId(), "PENDING")
                .forEach(pending -> pending.setStatus("SUPERSEDED"));
        DoctorPatientAccessRequest request = new DoctorPatientAccessRequest();
        request.setDoctor(doctor); request.setPatient(patient);
        request.setOtp(String.format("%06d", random.nextInt(1_000_000)));
        request.setCreatedAt(now); request.setExpiresAt(now.plusMinutes(otpExpirationMinutes));
        request.setStatus("PENDING"); request.setPurpose(purpose);
        request = requests.save(request);

        if (!Boolean.FALSE.equals(patient.getDoctorOtpNotificationsEnabled())) {
            PatientNotification notification = new PatientNotification();
            notification.setPatient(patient); notification.setAccessRequestId(request.getId());
            notification.setType("ACCESS_REQUEST"); notification.setTitle("Doctor Access Request");
            String doctorName = doctor.getDoctorDetails() == null ? "Doctor" : doctor.getDoctorDetails().getFullName();
            notification.setMessage(String.format("%s requested access to your medical history. Purpose: %s",
                    doctorName == null ? "Doctor" : doctorName, purpose == null ? "Not specified" : purpose));
            notification.setOtp(request.getOtp()); notification.setExpiresAt(request.getExpiresAt());
            notification.setCreatedAt(now); notification.setRead(false);
            notifications.save(notification);
        }
        return response(request);
    }

    @Transactional
    public PatientAccessResponse verify(String doctorInput, Long requestId, String patientInput, String otp) {
        User doctor = requireRole(doctorInput, "DOCTOR");
        User patient = requireRole(patientInput, "USER");
        DoctorPatientAccessRequest request = requests.findById(requestId)
                .orElseThrow(() -> new InvalidInputException("Access request not found"));
        if (!request.getDoctor().getId().equals(doctor.getId())) throw new InvalidInputException("Access request belongs to a different doctor");
        if (!request.getPatient().getId().equals(patient.getId())) throw new InvalidInputException("Access request belongs to a different patient");
        if ("VERIFIED".equals(request.getStatus())) throw new InvalidInputException("OTP already used; access request already verified");
        if (!"PENDING".equals(request.getStatus())) throw new InvalidInputException("Access request is not pending");
        if (!LocalDateTime.now().isBefore(request.getExpiresAt())) {
            request.setStatus("EXPIRED"); requests.save(request);
            throw new InvalidInputException("OTP expired");
        }
        if (otp == null || !request.getOtp().equals(otp.trim())) throw new InvalidInputException("Invalid OTP");
        request.setStatus("VERIFIED"); request.setUsedAt(LocalDateTime.now());
        requests.save(request);
        return response(request);
    }

    @Transactional(readOnly = true)
    public boolean hasVerifiedAccess(String doctorInput, String patientInput) {
        User doctor = requireRole(doctorInput, "DOCTOR");
        User patient = requireRole(patientInput, "USER");
        return requests.findFirstByDoctorIdAndPatientIdAndStatusOrderByCreatedAtDesc(doctor.getId(), patient.getId(), "VERIFIED").isPresent();
    }

    @Transactional(readOnly = true)
    public List<PatientAccessRequestDTO> patientRequests(String patientInput) {
        User patient = requireRole(patientInput, "USER");
        return requests.findByPatientIdOrderByCreatedAtDesc(patient.getId()).stream().map(this::patientRequestDto).toList();
    }

    @Transactional(readOnly = true)
    public List<PatientAccessRequestDTO> doctorRequests(String doctorInput, String patientInput) {
        User doctor = requireRole(doctorInput, "DOCTOR");
        if (patientInput == null || patientInput.isBlank())
            return requests.findByDoctorIdOrderByCreatedAtDesc(doctor.getId()).stream().map(r -> {
                PatientAccessRequestDTO dto = patientRequestDto(r); dto.setOtp(null); return dto;
            }).toList();
        User patient = requireRole(patientInput, "USER");
        return requests.findByDoctorIdAndPatientIdOrderByCreatedAtDesc(doctor.getId(), patient.getId()).stream().map(r -> {
            PatientAccessRequestDTO dto = patientRequestDto(r); dto.setOtp(null); return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<PatientNotificationDTO> notifications(String patientInput) {
        User patient = requireRole(patientInput, "USER");
        return notifications.findByPatientIdOrderByCreatedAtDesc(patient.getId()).stream().map(n -> {
            PatientNotificationDTO dto = new PatientNotificationDTO();
            dto.setId(n.getId()); dto.setAccessRequestId(n.getAccessRequestId()); dto.setType(n.getType());
            dto.setTitle(n.getTitle()); dto.setMessage(n.getMessage());
            dto.setExpiresAt(n.getExpiresAt()); dto.setTimestamp(n.getCreatedAt()); dto.setRead(n.isRead());
            dto.setStatus(notificationStatus(n));
            if ("PENDING".equals(dto.getStatus())) dto.setOtp(n.getOtp());
            return dto;
        }).toList();
    }

    @Transactional
    public PatientNotificationDTO markRead(String patientInput, Long notificationId) {
        User patient = requireRole(patientInput, "USER");
        PatientNotification n = notifications.findById(notificationId)
                .filter(item -> item.getPatient().getId().equals(patient.getId()))
                .orElseThrow(() -> new InvalidInputException("Notification not found"));
        n.setRead(true); notifications.save(n);
        PatientNotificationDTO dto = new PatientNotificationDTO();
        dto.setId(n.getId()); dto.setAccessRequestId(n.getAccessRequestId()); dto.setType(n.getType());
        dto.setTitle(n.getTitle()); dto.setMessage(n.getMessage());
        dto.setExpiresAt(n.getExpiresAt()); dto.setTimestamp(n.getCreatedAt()); dto.setRead(true);
        dto.setStatus(notificationStatus(n));
        if ("PENDING".equals(dto.getStatus())) dto.setOtp(n.getOtp());
        return dto;
    }

    @Transactional(readOnly = true)
    public PatientSettingsDTO getSettings(String patientInput) {
        return new PatientSettingsDTO(!Boolean.FALSE.equals(requireRole(patientInput, "USER").getDoctorOtpNotificationsEnabled()));
    }

    @Transactional
    public PatientSettingsDTO updateSettings(String patientInput, boolean enabled) {
        User patient = requireRole(patientInput, "USER"); patient.setDoctorOtpNotificationsEnabled(enabled); users.save(patient);
        return new PatientSettingsDTO(enabled);
    }

    private User requireRole(String accountId, String role) {
        if (accountId == null || accountId.isBlank()) throw new InvalidInputException("accountId");
        User user = users.findByAadhaarNumber(accountId).orElseThrow(() -> new AadhaarNotFoundException(accountId));
        if (!role.equals(user.getRole())) throw new InvalidInputException("Account is not a " + ("USER".equals(role) ? "patient" : "doctor"));
        return user;
    }

    private PatientAccessResponse response(DoctorPatientAccessRequest request) {
        return new PatientAccessResponse(request.getId(), request.getStatus(), request.getCreatedAt(), request.getExpiresAt());
    }

    private PatientAccessRequestDTO patientRequestDto(DoctorPatientAccessRequest request) {
        PatientAccessRequestDTO dto = new PatientAccessRequestDTO();
        User doctor = request.getDoctor(); User patient = request.getPatient();
        dto.setRequestId(request.getId()); dto.setDoctorAccountId(doctor.getAadhaarNumber());
        dto.setDoctorName(doctor.getDoctorDetails() == null ? "Doctor" : doctor.getDoctorDetails().getFullName());
        dto.setDoctorSpecialization(doctor.getDoctorDetails() == null ? null : doctor.getDoctorDetails().getSpecialization());
        dto.setHospitalName(doctor.getDoctorDetails() == null ? null : doctor.getDoctorDetails().getHospitalName());
        dto.setPatientAccountId(patient.getAadhaarNumber());
        dto.setPatientName(patient.getUserDetails() == null ? null : patient.getUserDetails().getFullName());
        dto.setPurpose(request.getPurpose());
        String status = request.getStatus();
        if ("PENDING".equals(status) && !LocalDateTime.now().isBefore(request.getExpiresAt())) status = "EXPIRED";
        dto.setStatus(status);
        dto.setOtp("PENDING".equals(status) ? request.getOtp() : null);
        dto.setCreatedAt(request.getCreatedAt()); dto.setExpiresAt(request.getExpiresAt());
        return dto;
    }

    private String notificationStatus(PatientNotification notification) {
        if (notification.getAccessRequestId() == null) return null;
        return requests.findById(notification.getAccessRequestId()).map(request -> {
            if ("PENDING".equals(request.getStatus()) && !LocalDateTime.now().isBefore(request.getExpiresAt())) return "EXPIRED";
            return request.getStatus();
        }).orElse("NOT_FOUND");
    }
}
