package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class PatientSettingsDTO {
    private boolean doctorOtpNotificationsEnabled;
}
