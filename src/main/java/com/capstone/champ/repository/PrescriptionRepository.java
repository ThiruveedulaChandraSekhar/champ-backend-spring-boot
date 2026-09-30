package com.capstone.champ.repository;

import com.capstone.champ.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByMedicineNameContainingIgnoreCase(String medicineName);
    Optional<Prescription> findByIdAndVisitUserIdAndMedicine_Id(Long id, Long userId, Long medicineId);

    @Query("select p from Prescription p left join fetch p.medicine join fetch p.visit v "
            + "where p.userFeedback is not null and trim(p.userFeedback) <> '' order by v.issueDate desc, p.id desc")
    List<Prescription> findAllWithPatientFeedback();

    @Query("select p from Prescription p left join fetch p.medicine "
            + "where p.visit.user.id = :userId and (p.medicine.id = :medicineId "
            + "or lower(trim(p.medicineName)) = lower(trim(:medicineName))) order by p.id")
    List<Prescription> findFeedbackByMedicineAndPatient(@Param("userId") Long userId,
                                                         @Param("medicineId") Long medicineId,
                                                         @Param("medicineName") String medicineName);
}