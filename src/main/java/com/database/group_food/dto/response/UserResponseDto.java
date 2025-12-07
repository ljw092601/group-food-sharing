package com.database.group_food.dto.response;

import com.database.group_food.domain.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private Long userId;
    private String nickname;
    private Double trustScore;
    private boolean isPhoneVerified;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.trustScore = user.getTrustScore();
        this.isPhoneVerified = user.isPhoneVerified();
    }
}