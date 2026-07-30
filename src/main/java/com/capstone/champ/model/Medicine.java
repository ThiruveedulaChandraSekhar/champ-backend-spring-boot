package com.capstone.champ.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "visit_id")
    private Visit visit;
}
