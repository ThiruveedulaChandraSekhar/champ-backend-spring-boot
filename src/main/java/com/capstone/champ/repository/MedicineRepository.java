package com.capstone.champ.repository;

import com.capstone.champ.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByMedicineNameContainingIgnoreCase(String medicineName);
}
