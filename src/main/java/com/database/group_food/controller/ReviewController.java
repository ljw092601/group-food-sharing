// src/main/java/com/database/group_food/controller/ReviewController.java
package com.database.group_food.controller;

import com.database.group_food.domain.User;
import com.database.group_food.dto.request.ReviewCreateRequestDto;
import com.database.group_food.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 후기 작성 API
     * POST /api/posts/{postId}/reviews
     */
    @PostMapping("/posts/{postId}/reviews")
    public ResponseEntity<?> createReview(
            @PathVariable Long postId,
            @RequestBody ReviewCreateRequestDto requestDto,
            @AuthenticationPrincipal User reviewer
    ) {
        try {
            reviewService.createReview(reviewer, postId, requestDto);
            return ResponseEntity.ok("Review submitted and Trust Score updated!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}