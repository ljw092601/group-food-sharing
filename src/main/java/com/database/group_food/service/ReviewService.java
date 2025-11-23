// src/main/java/com/database/group_food/service/ReviewService.java
package com.database.group_food.service;

import com.database.group_food.domain.*;
import com.database.group_food.dto.request.ReviewCreateRequestDto;
import com.database.group_food.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final TransactionReviewRepository reviewRepository;
    private final CoBuyPostRepository postRepository;
    private final UserRepository userRepository;
    private final CoBuyParticipantRepository participantRepository;

    @Transactional
    public void createReview(User reviewer, Long postId, ReviewCreateRequestDto requestDto) {

        // 1. 게시글 및 대상 조회
        CoBuyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        User reviewee = post.getHostUser();

        // 2. 검증 로직
        // A. 거래가 완료된 상태인가?
        if (post.getStatus() != CoBuyStatus.COMPLETED) {
            throw new IllegalStateException("You can only review after the transaction is COMPLETED.");
        }

        // B. 평가자가 실제 거래 참여자(또는 방장)인가?
        boolean isHost = post.getHostUser().equals(reviewer);
        boolean isParticipant = participantRepository.existsByPostAndParticipantUser(post, reviewer);

        if (!isHost && !isParticipant) {
            throw new IllegalAccessError("You are not a member of this transaction.");
        }

        // C. 중복 평가 방지
        if (reviewRepository.existsByPostAndReviewerAndReviewee(post, reviewer, reviewee)) {
            throw new IllegalStateException("You already reviewed this user for this post.");
        }

        // 3. 후기 저장 (INSERT)
        TransactionReview review = new TransactionReview();
        review.setPost(post);
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setRating(requestDto.getRating());
        review.setComment(requestDto.getComment());

        reviewRepository.save(review);

        // 4. [Trigger 역할] 신뢰도 점수 자동 갱신
        updateTrustScore(reviewee);
    }

    // 신뢰도 계산 및 업데이트 로직
    private void updateTrustScore(User user) {
        Double avgRating = reviewRepository.getAverageRatingForUser(user);

        if (avgRating != null) {
            // 공식: 평점(1~5) * 20 = 100점 만점 환산
            // 예: 평균 4.5점 -> 90점
            double newScore = avgRating * 20.0;
            user.setTrustScore(newScore); // User 엔티티 업데이트 (Dirty Checking으로 자동 DB 반영)
        }
    }
}