package com.capstone.champ.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "diagnosis", uniqueConstraints = @UniqueConstraint(name = "uk_diagnosis_code", columnNames = "diagnosis_code"))
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Diagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diagnosis_code", length = 50)
    private String diagnosisCode;

    @Column(nullable = false, length = 255)
    private String diagnosisName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "diagnosis")
    private List<Visit> visits;
}