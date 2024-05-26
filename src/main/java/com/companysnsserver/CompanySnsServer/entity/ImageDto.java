package com.companysnsserver.CompanySnsServer.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ImageDto {
    private Long id;
    private String imageUrl;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private String userid;
    private String username;

    public ImageDto(Long id, String imageUrl, String title, String description, LocalDateTime createdAt, String userid, String username) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.userid = userid;
        this.username = username;
    }

    // Getters and Setters
}
