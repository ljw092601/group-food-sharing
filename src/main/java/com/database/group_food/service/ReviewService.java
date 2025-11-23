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
        // 1. 실제 받은 평점 평균 (데이터가 없으면 0.0)
        Double realAvg = reviewRepository.getAverageRatingForUser(user);
        if (realAvg == null) realAvg = 0.0;

        // 2. 리뷰 개수 (거래 횟수)
        long count = reviewRepository.countByReviewee(user);

        // --- [베이지안 평균 계산] ---
        // C: 가중치 기준 점수 (보통 3.5점이나 전체 평균을 사용. 여기선 3.5로 고정)
        double C = 3.5;
        // m: 가중치를 주기 위한 최소 리뷰 수 (이 숫자보다 적으면 C에 가깝게 나옴)
        double m = 3.0;

        // 공식: (count / (count + m)) * realAvg + (m / (count + m)) * C
        double bayesianAvg = (count / (count + m)) * realAvg + (m / (count + m)) * C;

        // 100점 만점으로 환산
        double baseScore = bayesianAvg * 20.0;

        // --- [활동 가산점] ---
        // 거래 1회당 0.5점 추가 (최대 10점까지만)
        double bonusScore = Math.min(count * 0.5, 10.0);

        // 최종 점수 합산 (최대 100점 넘지 않게)
        double finalScore = Math.min(baseScore + bonusScore, 100.0);

        // 소수점 1자리까지만 남기기 (깔끔하게)
        finalScore = Math.round(finalScore * 10.0) / 10.0;

        // 3. DB 업데이트
        user.setTrustScore(finalScore);
    }
}