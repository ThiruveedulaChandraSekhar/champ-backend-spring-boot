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
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String gender;
    private String mobileNumber;
    private String emergencyContact;
    private String email;
    private String aadhaarNumber;
    private String guardian;
    private String guardianContact;
    private LocalDate dateOfBirth;

    @OneToOne(mappedBy = "userDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;
    private String bloodGroup;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
