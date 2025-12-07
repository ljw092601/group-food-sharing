package com.database.group_food.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequestDto {
    private Long revieweeId; // 평가 받는 사람의 ID (누구를 평가할 것인가)
    private int rating;      // 별점 (1~5)
    private String comment;  // 한줄 평
}