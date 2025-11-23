// src/main/java/com/database/group_food/dto/response/UserResponseDto.java
package com.database.group_food.dto.response;

import com.database.group_food.domain.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private Long userId;
    private String nickname;
    private Double trustScore;
    private boolean isPhoneVerified;
    // 위치 정보는 필요하다면 추가 (보안상 타인 조회 시엔 뺄 수도 있음)

    // Entity -> DTO 변환 생성자
    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.trustScore = user.getTrustScore();
        this.isPhoneVerified = user.isPhoneVerified();
    }
}