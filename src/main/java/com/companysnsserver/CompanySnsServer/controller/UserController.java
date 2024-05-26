package com.companysnsserver.CompanySnsServer.controller;
import com.companysnsserver.CompanySnsServer.entity.Image;
import com.companysnsserver.CompanySnsServer.entity.ImageDto;
import com.companysnsserver.CompanySnsServer.entity.User;
import com.companysnsserver.CompanySnsServer.entity.UserFollow;
import com.companysnsserver.CompanySnsServer.repository.ImageRepository;
import com.companysnsserver.CompanySnsServer.repository.UserFollowRepository;
import com.companysnsserver.CompanySnsServer.repository.UserRepository;
import com.companysnsserver.CompanySnsServer.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserFollowRepository userFollowRepository;

    private final Pattern alphanumeric = Pattern.compile("^[a-zA-Z0-9]+$");

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        if (!alphanumeric.matcher(user.getUserid()).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "유저네임은 대소문자와 숫자가 포함되어야 합니다."));
        }
        if (!alphanumeric.matcher(user.getPassword()).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "비밀번호는 대소문자와 숫자가 포함되어야 합니다."));
        }
        if (userRepository.findByUserid(user.getUserid()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "유저네임이 이미 존재합니다"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(Collections.singletonMap("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        User foundUser = userRepository.findByUserid(user.getUserid());
        if (foundUser != null && passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userid", foundUser.getUserid()); // userid를 응답에 포함
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("success", false));
    }

    @GetMapping("/profile/{userid}")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable("userid") String userid) {
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyMap());
        }

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("userid", user.getUserid());
        profileData.put("name", user.getName());
        profileData.put("profileImage", user.getProfileImage() != null ? user.getProfileImage() : "https://companysnsspringboot.s3.ap-northeast-2.amazonaws.com/default-profile.png");
        //profileData.put("images", user.getImages() != null ? user.getImages() : new ArrayList<>());
        return ResponseEntity.ok(profileData);
    }

    @PostMapping("/profile/upload")
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestParam("file") MultipartFile file, @RequestParam("userid") String userid) {
        if (file == null || userid == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "File or userid is missing"));
        }

        String fileUrl = s3Service.uploadFile(file);
        User user = userRepository.findByUserid(userid);
        if (user != null) {
            user.setProfileImage(fileUrl);
            userRepository.save(user);
        }
        return ResponseEntity.ok(Map.of("url", fileUrl));
    }

    @GetMapping("/images")
    public List<Image> getImages(@RequestParam("userid") String userid) {
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return imageRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @PostMapping("/follow")
    public ResponseEntity<String> followUser(@RequestParam("userid") String userid, @RequestParam("followUserid") String followUserid) {
        User user = userRepository.findByUserid(userid);
        User followUser = userRepository.findByUserid(followUserid);

        if (user == null || followUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        // 중복 팔로우 방지
        if (userFollowRepository.findByUserIdAndFollowUserIdOrUserIdAndFollowUserId(user.getId(), followUser.getId(), followUser.getId(), user.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already following");
        }

        UserFollow userFollow = new UserFollow(user, followUser);
        userFollowRepository.save(userFollow);

        return ResponseEntity.ok("Followed successfully");
    }



    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(@RequestParam("userid") String userid, @RequestParam("followUserid") String followUserid) {
        User user = userRepository.findByUserid(userid);
        User followUser = userRepository.findByUserid(followUserid);

        if (user == null || followUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        // 두 사용자의 연결 중 존재하는 경우에만 삭제
        userFollowRepository.findByUserIdAndFollowUserId(user.getId(), followUser.getId()).ifPresent(userFollowRepository::delete);
        userFollowRepository.findByUserIdAndFollowUserId(followUser.getId(), user.getId()).ifPresent(userFollowRepository::delete);

        return ResponseEntity.ok("Unfollowed successfully");
    }



    @GetMapping("/followingPosts")
    public List<Image> getFollowingPosts(@RequestParam("userid") String userid) {
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<UserFollow> following = userFollowRepository.findByUser(user);
        List<Image> followingPosts = new ArrayList<>();
        for (UserFollow followee : following) {
            List<Image> images = followee.getFollowUser().getImages();
            if (images != null) {
                followingPosts.addAll(images);
            }
        }

        followingPosts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return followingPosts;
    }



    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam("userid") String userid, @RequestParam("query") String query) {
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return userRepository.findByUseridContaining(query)
                .stream()
                .filter(u -> !u.getUserid().equals(userid))
                .collect(Collectors.toList());
    }

    @GetMapping("/followers")
    public List<User> getFollowers(@RequestParam("userid") String userid) {
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<UserFollow> followers = userFollowRepository.findByFollowUser(user);
        return followers.stream()
                .map(UserFollow::getUser)
                .collect(Collectors.toList());
    }

    @GetMapping("/following")
    public List<User> getFollowing(@RequestParam("userid") String userid) {
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<UserFollow> following = userFollowRepository.findByUser(user);
        return following.stream()
                .map(UserFollow::getFollowUser)
                .collect(Collectors.toList());
    }


    @GetMapping("/isFollowing")
    public ResponseEntity<Boolean> isFollowing(@RequestParam("userid") String userid, @RequestParam("followUserid") String followUserid) {
        User user = userRepository.findByUserid(userid);
        User followUser = userRepository.findByUserid(followUserid);

        if (user == null || followUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }

        boolean isFollowing = userFollowRepository.findByUserIdAndFollowUserIdOrUserIdAndFollowUserId(user.getId(), followUser.getId(), followUser.getId(), user.getId()).isPresent();
        return ResponseEntity.ok(isFollowing);
    }

    @GetMapping("/connections")
    public List<User> getConnections(@RequestParam("userid") String userid) {
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // User가 팔로우한 사람들
        List<UserFollow> following = userFollowRepository.findByUser(user);
        List<User> connections = following.stream()
                .map(UserFollow::getFollowUser)
                .collect(Collectors.toList());

        // User를 팔로우한 사람들
        List<UserFollow> followers = userFollowRepository.findByFollowUser(user);
        List<User> followersList = followers.stream()
                .map(UserFollow::getUser)
                .collect(Collectors.toList());

        // 양쪽을 모두 포함한 연결된 사용자 리스트 반환
        connections.addAll(followersList);
        return connections;
    }

    @GetMapping("/allPosts")
    public List<ImageDto> getAllPosts(@RequestParam("userid") String userid) {
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Set<Image> allPosts = new HashSet<>(user.getImages()); // 자신의 게시물을 추가합니다.

        // 자신을 팔로우한 사용자들의 게시물도 추가합니다.
        List<UserFollow> followers = userFollowRepository.findByFollowUser(user);
        for (UserFollow follower : followers) {
            allPosts.addAll(follower.getUser().getImages());
        }

        // 팔로우한 사람들의 게시물도 추가합니다.
        List<UserFollow> following = userFollowRepository.findByUser(user);
        for (UserFollow followee : following) {
            allPosts.addAll(followee.getFollowUser().getImages());
        }

        // List로 변환 후 최신 순으로 정렬
        List<ImageDto> sortedPosts = new ArrayList<>();
        for (Image image : allPosts) {
            sortedPosts.add(new ImageDto(
                    image.getId(),
                    image.getImageUrl(),
                    image.getTitle(),
                    image.getDescription(),
                    image.getCreatedAt(),
                    image.getUser().getUserid(),
                    image.getUser().getName()
            ));
        }
        sortedPosts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return sortedPosts;
    }


}
