package com.companysnsserver.CompanySnsServer.controller;

import com.companysnsserver.CompanySnsServer.entity.Image;
import com.companysnsserver.CompanySnsServer.entity.User;
import com.companysnsserver.CompanySnsServer.repository.ImageRepository;
import com.companysnsserver.CompanySnsServer.repository.UserRepository;
import com.companysnsserver.CompanySnsServer.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/image")
public class ImageUploadController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private S3Service s3Service;
    private MultipartFile file;
    private String userid;
    private String title;
    private String description;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userid") String userid,
            @RequestParam("title") String title,
            @RequestParam("description") String description) {
        this.file = file;
        this.userid = userid;
        this.title = title;
        this.description = description;
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        String imageUrl = s3Service.uploadFile(file);
        Image image = new Image();
        image.setUser(user);
        image.setImageUrl(imageUrl);
        image.setTitle(title);
        image.setDescription(description);
        image.setCreatedAt(LocalDateTime.now()); // 현재 시간 설정
        imageRepository.save(image);

        return ResponseEntity.ok("Image uploaded successfully");
    }

    @GetMapping("/images/{userid}")
    public ResponseEntity<List<Map<String, String>>> getUserImages(@PathVariable String userid) {
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }

        List<Image> images = imageRepository.findByUserId(user.getId());
        List<Map<String, String>> imageList = images.stream().map(image -> Map.of(
                "imageUrl", image.getImageUrl(),
                "title", image.getTitle(),
                "description", image.getDescription()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(imageList);
    }
}
