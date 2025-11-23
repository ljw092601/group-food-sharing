// src/main/java/com/database/group_food/controller/ParticipationController.java
package com.database.group_food.controller;

import com.database.group_food.domain.User;
import com.database.group_food.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts/{postId}/participate")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping
    public ResponseEntity<?> joinPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user
    ) {
        try {
            participationService.joinPost(user, postId);
            return ResponseEntity.ok("Successfully joined the Co-Buy!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}