package com.capstone.champ.repository;

import com.capstone.champ.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    boolean existsByDiagnosisNameIgnoreCase(String diagnosisName);
    boolean existsByDiagnosisCodeIgnoreCase(String diagnosisCode);
}