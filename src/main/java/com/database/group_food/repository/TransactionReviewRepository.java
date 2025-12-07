package com.database.group_food.repository;

import com.database.group_food.domain.CoBuyPost;
import com.database.group_food.domain.TransactionReview;
import com.database.group_food.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionReviewRepository extends JpaRepository<TransactionReview, Long> {

    // 중복 평가 방지용
    boolean existsByPostAndReviewerAndReviewee(CoBuyPost post, User reviewer, User reviewee);

    boolean existsByPostAndReviewer(CoBuyPost post, User reviewer);

    // 특정 유저가 받은 점수의 '평균'을 계산하는 쿼리
    @Query("SELECT AVG(r.rating) FROM TransactionReview r WHERE r.reviewee = :user")
    Double getAverageRatingForUser(@Param("user") User user);

    long countByReviewee(User reviewee);
}