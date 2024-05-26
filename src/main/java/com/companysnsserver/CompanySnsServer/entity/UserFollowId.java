package com.companysnsserver.CompanySnsServer.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class UserFollowId implements Serializable {
    private Long userId;
    private Long followUserId;

    // 기본 생성자
    public UserFollowId() {}

    // 생성자
    public UserFollowId(Long userId, Long followUserId) {
        this.userId = userId;
        this.followUserId = followUserId;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFollowUserId() {
        return followUserId;
    }

    public void setFollowUserId(Long followUserId) {
        this.followUserId = followUserId;
    }
}


