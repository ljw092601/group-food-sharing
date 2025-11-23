// src/main/java/com/database/group_food/controller/PostController.java
package com.database.group_food.controller;

import com.database.group_food.domain.User;
import com.database.group_food.dto.request.PostCreateRequestDto;
import com.database.group_food.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.database.group_food.dto.response.PostResponseDto; // 임포트
import java.util.List;
import org.springframework.web.multipart.MultipartFile; // 임포트
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody PostCreateRequestDto requestDto,
            @AuthenticationPrincipal User user // Spring Security가 로그인한 유저 정보를 넣어줌
    ) {
        postService.createPost(user, requestDto);
        return ResponseEntity.ok("Post created successfully");
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getNearbyPosts(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "1000") double radius,
            @AuthenticationPrincipal User user // [필수] 현재 로그인한 유저 정보 받기
    ) {
        // 서비스에 user를 넘겨줍니다
        return ResponseEntity.ok(postService.getNearbyPosts(user, longitude, latitude, radius));
    }

    @PutMapping("/{postId}/complete")
    public ResponseEntity<?> completeTransaction(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user
    ) {
        try {
            postService.completeTransaction(user, postId);
            return ResponseEntity.ok("Transaction completed! Now you can write reviews.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/{postId}/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadReceipt(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) {
        try {
            String imageUrl = postService.uploadReceipt(user, postId, file);
            return ResponseEntity.ok("Receipt uploaded successfully. URL: " + imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}