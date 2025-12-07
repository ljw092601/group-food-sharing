package com.database.group_food.dto.response;

import com.database.group_food.domain.CoBuyPost;
import com.database.group_food.domain.CoBuyStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Getter
public class PostResponseDto {
    private Long postId;
    private String hostNickname;
    private String itemName;
    private String description;
    private int totalUnits;
    private int currentUnits;
    private BigDecimal pricePerUnit;
    private CoBuyStatus status;
    private String createdAt;
    private Long hostUserId;
    private String purchaseUrl;
    private Double hostTrustScore;

    @JsonProperty("isReviewed")
    private boolean isReviewed;

    @JsonProperty("isParticipant")
    private boolean isParticipant;

    private double longitude;
    private double latitude;

    public PostResponseDto(CoBuyPost entity, boolean isReviewed, boolean isParticipant) {
        this.postId = entity.getPostId();
        this.hostUserId = entity.getHostUser().getUserId();
        this.hostNickname = entity.getHostUser().getNickname();
        this.itemName = entity.getItemName();
        this.description = entity.getDescription();
        this.totalUnits = entity.getTotalUnits();
        this.currentUnits = entity.getCurrentUnits();
        this.pricePerUnit = entity.getPricePerUnit();
        this.status = entity.getStatus();
        this.createdAt = entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.hostTrustScore = entity.getHostUser().getTrustScore();

        this.longitude = entity.getLocation().getX();
        this.latitude = entity.getLocation().getY();
        this.purchaseUrl = entity.getPurchaseUrl();
        this.isReviewed = isReviewed;
        this.isParticipant = isParticipant;
    }
}