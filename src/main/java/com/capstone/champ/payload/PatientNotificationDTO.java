package com.capstone.champ.payload;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PatientNotificationDTO {
    private Long id;
    private Long accessRequestId;
    private String type;
    private String title;
    private String message;
    private String otp;
    private LocalDateTime expiresAt;
    private LocalDateTime timestamp;
    private boolean read;
    private String status;
}
