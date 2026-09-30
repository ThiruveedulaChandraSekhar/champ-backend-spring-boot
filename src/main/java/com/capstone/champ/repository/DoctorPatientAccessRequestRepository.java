package com.capstone.champ.repository;

import com.capstone.champ.model.DoctorPatientAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface DoctorPatientAccessRequestRepository extends JpaRepository<DoctorPatientAccessRequest, Long> {
    Optional<DoctorPatientAccessRequest> findFirstByDoctorIdAndPatientIdAndStatusOrderByCreatedAtDesc(Long doctorId, Long patientId, String status);
    List<DoctorPatientAccessRequest> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<DoctorPatientAccessRequest> findByDoctorIdAndPatientIdOrderByCreatedAtDesc(Long doctorId, Long patientId);
    List<DoctorPatientAccessRequest> findByDoctorIdAndPatientIdAndStatus(Long doctorId, Long patientId, String status);
    List<DoctorPatientAccessRequest> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);
}
