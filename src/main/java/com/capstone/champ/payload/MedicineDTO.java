package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineDTO {
    private Long id;
    private String medicineName;
    private String dosage;
    private Boolean isInjection;
    private Integer duration;
    private Boolean takeMorning;
    private Boolean takeAfternoon;
    private Short easeOfUse;
    private String userFeedback;
    private Boolean takeEvening;
    private String note;
}
