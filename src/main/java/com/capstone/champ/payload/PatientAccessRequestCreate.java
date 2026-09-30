package com.capstone.champ.payload;

import lombok.Data;

@Data
public class PatientAccessRequestCreate {
    private String patientAccountId;
    private String purpose;
}
