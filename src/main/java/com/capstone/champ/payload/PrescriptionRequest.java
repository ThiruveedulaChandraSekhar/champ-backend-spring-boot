package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionRequest {
    private Long medicineId;
    private String dosage;
    private Boolean isInjection;
    private Integer duration;
    private Boolean takeMorning;
    private Boolean takeAfternoon;
    private Boolean takeEvening;
    private Short easeOfUse;
    private String userFeedback;
    private String note;
}
