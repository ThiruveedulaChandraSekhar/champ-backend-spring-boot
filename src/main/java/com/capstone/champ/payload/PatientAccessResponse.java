package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data @AllArgsConstructor
public class PatientAccessResponse {
    private Long requestId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
