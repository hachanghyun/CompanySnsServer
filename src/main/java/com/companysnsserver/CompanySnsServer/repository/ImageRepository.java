package com.companysnsserver.CompanySnsServer.repository;

import com.companysnsserver.CompanySnsServer.entity.Image;
import com.companysnsserver.CompanySnsServer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUserId(Long userId);

    List<Image> findByUserOrderByCreatedAtDesc(User user);
}

