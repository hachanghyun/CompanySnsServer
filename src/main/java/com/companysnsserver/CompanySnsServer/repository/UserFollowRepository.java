package com.companysnsserver.CompanySnsServer.repository;

import com.companysnsserver.CompanySnsServer.entity.User;
import com.companysnsserver.CompanySnsServer.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    List<UserFollow> findByUser(User user);
    List<UserFollow> findByFollowUser(User followUser);
    UserFollow findByUserAndFollowUser(User user, User followUser);
    //UserFollow findByUserIdAndFollowUserId(Long userId, Long followUserId);
    void deleteByUserIdAndFollowUserId(Long userId, Long followUserId);
    void deleteByUserIdOrFollowUserId(Long userId, Long followUserId);
    Optional<UserFollow> findByUserIdAndFollowUserId(Long userId, Long followUserId);
    Optional<UserFollow> findByUserIdAndFollowUserIdOrUserIdAndFollowUserId(Long userId, Long followUserId, Long followUserId2, Long userId2);
}

