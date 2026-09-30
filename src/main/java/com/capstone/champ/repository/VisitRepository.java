package com.capstone.champ.repository;

import com.capstone.champ.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByUserId(Long userId);
    List<Visit> findByUserIdAndDoctorDetailsUserId(Long userId, Long doctorUserId);

    @Query("select distinct v from Visit v left join fetch v.medicines m left join fetch m.medicine "
	    + "where v.user.id = :userId and v.id in :visitIds")
    List<Visit> findWithMedicines(@Param("userId") Long userId, @Param("visitIds") Collection<Long> visitIds);

    @Query("select distinct v from Visit v left join fetch v.allergies "
	    + "where v.user.id = :userId and v.id in :visitIds")
    List<Visit> findWithAllergies(@Param("userId") Long userId, @Param("visitIds") Collection<Long> visitIds);
}
