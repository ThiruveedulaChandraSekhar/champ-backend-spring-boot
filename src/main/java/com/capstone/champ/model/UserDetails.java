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
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String gender;
    private String emergencyContact;
    private String email;
    private String guardian;
    private String guardianContact;
    private LocalDate dateOfBirth;
    private LocalDate lastUpdated;
    private LocalDate created;
    private String bloodGroup;
    private Boolean verificationStatus;
    private String personalImage;
    @Lob
    private byte[] template;

    @OneToOne(mappedBy = "userDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

}
