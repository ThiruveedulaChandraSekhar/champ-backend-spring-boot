package com.capstone.champ.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "medicine")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Prescription {
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
    private Double medicineSuccessProbability;
    private String medicinePredictionStatus;
    private LocalDateTime medicinePredictionGeneratedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @Transient
    private Long medicineId;
}