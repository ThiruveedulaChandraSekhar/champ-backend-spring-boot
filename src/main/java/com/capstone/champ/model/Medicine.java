package com.capstone.champ.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "medicine_catalog")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String medicineName;

    @Column(length = 255)
    private String activeIngredient;

    @Column(length = 100)
    private String strength;

    @Column(length = 100)
    private String dosageForm;

    @Column(length = 50)
    private String atcCode;

    @OneToMany(mappedBy = "medicine")
    private List<Prescription> prescriptions;
}
