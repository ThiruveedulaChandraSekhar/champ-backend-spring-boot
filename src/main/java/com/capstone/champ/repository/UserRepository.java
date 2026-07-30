package com.capstone.champ.repository;

import com.capstone.champ.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
