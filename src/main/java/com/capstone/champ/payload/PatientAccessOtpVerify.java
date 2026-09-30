package com.capstone.champ.payload;

import lombok.Data;

@Data
public class PatientAccessOtpVerify {
    private Long requestId;
    private String patientAccountId;
    private String otp;
}
