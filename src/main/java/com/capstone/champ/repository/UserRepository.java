package com.capstone.champ.repository;

import com.capstone.champ.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAadhaarNumber(String aadhaarNumber);

    Optional<User> findByMobileNumber(String input);
}
