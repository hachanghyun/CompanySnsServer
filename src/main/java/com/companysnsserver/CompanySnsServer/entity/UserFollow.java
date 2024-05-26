package com.companysnsserver.CompanySnsServer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserFollow {

    @EmbeddedId
    private UserFollowId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("followUserId")
    @JoinColumn(name = "follow_user_id")
    private User followUser;

    // 기본 생성자
    public UserFollow() {}

    // 생성자
    public UserFollow(User user, User followUser) {
        this.user = user;
        this.followUser = followUser;
        this.id = new UserFollowId(user.getId(), followUser.getId());
    }
}


