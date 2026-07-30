package com.capstone.champ.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String hospitalName;
    private String specialization;
    private LocalDate dateOfBirth;
    private String gender;
    private String yearOfRegistration;
    private String medicalCouncilState;
    private String registrationNumber;
    private Boolean verificationStatus;
    private String personalImage;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "doctorDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Allergy> allergies;

    @OneToMany(mappedBy = "doctorDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits;

}
