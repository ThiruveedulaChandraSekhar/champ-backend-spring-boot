package com.capstone.champ.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pinCode;
    private String doorNumber;
    private String street;
    private String city;
    private String state;

    @OneToOne
    @JoinColumn(name = "user_details_id")
    private UserDetails userDetails;
}
