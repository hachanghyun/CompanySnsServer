package com.companysnsserver.CompanySnsServer.repository;

import com.companysnsserver.CompanySnsServer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// UserRepository.java
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserid(String userid);

    List<User> findByUseridContaining(String userid);
}