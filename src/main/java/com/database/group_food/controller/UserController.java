// src/main/java/com/database/group_food/controller/UserController.java
package com.database.group_food.controller;

import com.database.group_food.domain.User;
import com.database.group_food.dto.request.LocationUpdateRequestDto;
import com.database.group_food.dto.response.UserResponseDto;
import com.database.group_food.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 1. 내 프로필 조회
     * GET /api/users/me
     * - 헤더에 토큰이 있어야 함
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyProfile(@AuthenticationPrincipal User user) {
        // @AuthenticationPrincipal로 현재 로그인한 유저 객체를 바로 받음
        // (단, 엔티티가 Detached 상태일 수 있으므로 ID로 다시 조회하거나 DTO 변환)
        return ResponseEntity.ok(new UserResponseDto(user));
    }

    /**
     * 2. 타인 프로필 조회 (신뢰도 확인용)
     * GET /api/users/{userId}
     * - 예: /api/users/5 (5번 유저의 점수는?)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    /**
     * 3. 내 동네 위치 변경
     * PUT /api/users/me/location
     * - 이사 갔거나, 거래 지역을 바꾸고 싶을 때
     */
    @PutMapping("/me/location")
    public ResponseEntity<?> updateMyLocation(
            @AuthenticationPrincipal User user,
            @RequestBody LocationUpdateRequestDto requestDto
    ) {
        userService.updateLocation(user.getUserId(), requestDto.getLongitude(), requestDto.getLatitude());
        return ResponseEntity.ok("Location updated successfully.");
    }
}