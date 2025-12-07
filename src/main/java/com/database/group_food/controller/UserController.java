package com.database.group_food.controller;

import com.database.group_food.domain.User;
import com.database.group_food.dto.request.LocationUpdateRequestDto;
import com.database.group_food.dto.response.UserResponseDto;
import com.database.group_food.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.database.group_food.dto.response.PostResponseDto;
import com.database.group_food.service.PostService;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    //내 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyProfile(@AuthenticationPrincipal User user) {
        // @AuthenticationPrincipal로 현재 로그인한 유저 객체를 바로 받음
        // (단, 엔티티가 Detached 상태일 수 있으므로 ID로 다시 조회하거나 DTO 변환)
        return ResponseEntity.ok(new UserResponseDto(user));
    }


    // 타인 프로필 조회 (신뢰도 확인용)
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }


    // 내 동네 위치 변경
    @PutMapping("/me/location")
    public ResponseEntity<?> updateMyLocation(
            @AuthenticationPrincipal User user,
            @RequestBody LocationUpdateRequestDto requestDto
    ) {
        userService.updateLocation(user.getUserId(), requestDto.getLongitude(), requestDto.getLatitude());
        return ResponseEntity.ok("Location updated successfully.");
    }

    @GetMapping("/me/hosted")
    public ResponseEntity<List<PostResponseDto>> getMyHostedPosts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(postService.getMyHostedPosts(user));
    }

    // 내가 참여한 글 내역
    @GetMapping("/me/participated")
    public ResponseEntity<List<PostResponseDto>> getMyParticipatedPosts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(postService.getMyParticipatedPosts(user));
    }
}