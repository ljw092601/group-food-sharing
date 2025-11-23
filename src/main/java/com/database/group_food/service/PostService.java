// src/main/java/com/database/group_food/service/PostService.java
package com.database.group_food.service;

import com.database.group_food.domain.CoBuyPost;
import com.database.group_food.domain.CoBuyStatus;
import com.database.group_food.domain.User;
import com.database.group_food.dto.request.PostCreateRequestDto;
import com.database.group_food.repository.CoBuyPostRepository;
import com.database.group_food.repository.TransactionReviewRepository; // [추가] 리포지토리 임포트
import com.database.group_food.util.GeometryUtil;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.database.group_food.dto.response.PostResponseDto;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final CoBuyPostRepository postRepository;
    private final TransactionReviewRepository reviewRepository; // [추가] 후기 확인용 리포지토리 주입
    private final GeometryUtil geometryUtil;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public CoBuyPost createPost(User host, PostCreateRequestDto requestDto) {
        CoBuyPost post = new CoBuyPost();

        // 1. 기본 정보 매핑
        post.setHostUser(host);
        post.setItemName(requestDto.getItemName());
        post.setDescription(requestDto.getDescription());
        post.setTotalUnits(requestDto.getTotalUnits());
        post.setCurrentUnits(1);
        post.setPricePerUnit(BigDecimal.valueOf(requestDto.getPricePerUnit()));
        post.setStatus(CoBuyStatus.RECRUITING);

        // 2. 위치 정보 변환
        post.setLocation(geometryUtil.createPoint(requestDto.getLongitude(), requestDto.getLatitude()));

        // 3. 저장
        return postRepository.save(post);
    }

    // [수정] User currentUser 파라미터 추가 및 로직 변경
    @Transactional(readOnly = true)
    public List<PostResponseDto> getNearbyPosts(User currentUser, double longitude, double latitude, double radius) {
        Point myLocation = geometryUtil.createPoint(longitude, latitude);

        // 1. 주변 글 찾기
        List<CoBuyPost> posts = postRepository.findNearbyPosts(myLocation, radius);

        // 2. 각 글마다 '내가 후기 썼는지' 확인해서 DTO로 변환
        return posts.stream().map(post -> {
            boolean isReviewed = false;

            // 로그인한 유저라면 DB에서 확인
            if (currentUser != null) {
                isReviewed = reviewRepository.existsByPostAndReviewer(post, currentUser);
            }

            // DTO 생성 (생성자에 isReviewed 전달)
            return new PostResponseDto(post, isReviewed);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void completeTransaction(User user, Long postId) {
        CoBuyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getHostUser().getUserId().equals(user.getUserId())) {
            throw new IllegalAccessError("Only the host can complete the transaction.");
        }

        post.setStatus(CoBuyStatus.COMPLETED);
    }

    @Transactional
    public String uploadReceipt(User user, Long postId, MultipartFile file) throws IOException {
        CoBuyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getHostUser().getUserId().equals(user.getUserId())) {
            throw new IllegalAccessError("Only the host can upload the receipt.");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID() + "_" + originalFilename;
        String fullPath = uploadDir + savedFilename;

        file.transferTo(new File(fullPath));

        String accessUrl = "/images/" + savedFilename;
        post.setReceiptImageUrl(accessUrl);
        post.setStatus(CoBuyStatus.PURCHASED);

        return accessUrl;
    }
}