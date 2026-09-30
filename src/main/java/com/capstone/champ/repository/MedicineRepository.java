package com.capstone.champ.repository;

import com.capstone.champ.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByMedicineNameContainingIgnoreCase(String medicineName);
    boolean existsByMedicineNameIgnoreCase(String medicineName);

    @Query("select m from Medicine m where lower(trim(m.medicineName)) = lower(trim(:medicineName))")
    Optional<Medicine> findByNormalizedMedicineName(@Param("medicineName") String medicineName);
}
