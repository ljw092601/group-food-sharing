// src/main/java/com/database/group_food/dto/request/LocationUpdateRequestDto.java
package com.database.group_food.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationUpdateRequestDto {
    private double longitude;
    private double latitude;
}