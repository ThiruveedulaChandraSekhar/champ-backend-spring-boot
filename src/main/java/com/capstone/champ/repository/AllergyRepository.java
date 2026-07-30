package com.capstone.champ.repository;

import com.capstone.champ.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {
}
