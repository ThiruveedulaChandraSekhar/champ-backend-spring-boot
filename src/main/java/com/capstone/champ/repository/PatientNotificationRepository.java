package com.capstone.champ.repository;

import com.capstone.champ.model.PatientNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PatientNotificationRepository extends JpaRepository<PatientNotification, Long> {
    List<PatientNotification> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
