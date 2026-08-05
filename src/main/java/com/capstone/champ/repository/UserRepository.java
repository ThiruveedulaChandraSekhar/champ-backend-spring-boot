package com.capstone.champ.repository;

import com.capstone.champ.model.Role;
import com.capstone.champ.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAadhaarNumber(String aadhaarNumber);

    List<User> findByMobileNumber(String input);

    List<User> findByRoleAndVerificationStatus(String role, Boolean verificationStatus);
}
