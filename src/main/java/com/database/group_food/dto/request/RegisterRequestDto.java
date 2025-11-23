package com.database.group_food.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String nickname;
    private String password;
    private double longitude;
    private double latitude;
}