package com.capstone.champ.repository;

import com.capstone.champ.model.DoctorDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorDetailsRepository extends JpaRepository<DoctorDetails, Long> {
}
