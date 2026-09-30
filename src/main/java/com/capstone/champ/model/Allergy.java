package com.capstone.champ.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Allergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private Short severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "allergy_type")
    private AllergyType allergyType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "doctor_details_id")
    private DoctorDetails doctorDetails;

    @ManyToOne
    @JoinColumn(name = "visit_id")
    private Visit visit;
}
