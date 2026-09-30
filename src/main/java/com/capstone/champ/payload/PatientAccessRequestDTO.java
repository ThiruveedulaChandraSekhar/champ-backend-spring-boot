package com.capstone.champ.payload;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PatientAccessRequestDTO {
    private Long requestId;
    private String doctorAccountId;
    private String doctorName;
    private String doctorSpecialization;
    private String hospitalName;
    private String patientAccountId;
    private String patientName;
    private String purpose;
    private String status;
    private String otp;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
