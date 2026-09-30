package com.capstone.champ.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reason;
    private LocalDate issueDate;
    private LocalDate recoveredDate;
    private String prescriptionImage;
    private Double predictedRecoveryDays;
    private LocalDate predictedRecoveryDate;
    private String recoveryPredictionStatus;
    private LocalDateTime recoveryPredictionGeneratedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private RecoveryStatus recoveryStatus = RecoveryStatus.UNKNOWN;
    private LocalDateTime recoveryConfirmedAt;
    @Column(length = 50)
    private String outcomeSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id")
    private Diagnosis diagnosis;

    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> medicines;

    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Allergy> allergies;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "doctor_details_id")
    private DoctorDetails doctorDetails;
}
