// src/main/java/com/database/group_food/dto/request/PostCreateRequestDto.java
package com.database.group_food.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequestDto {
    private String itemName;      // 상품명 (예: 양파)
    private String description;   // [추가됨] 상세 내용
    private int totalUnits;       // 총 모집 인원 (예: 3)
    private double pricePerUnit;  // 1인당 가격 (예: 2500)
    private double longitude;     // 거래 희망 장소 (경도)
    private double latitude;      // 거래 희망 장소 (위도)
    private String purchaseUrl;
}