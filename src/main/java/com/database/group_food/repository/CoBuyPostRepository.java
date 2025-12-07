package com.database.group_food.repository;

import com.database.group_food.domain.CoBuyPost;
import com.database.group_food.domain.User;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

import java.util.List;

public interface CoBuyPostRepository extends JpaRepository<CoBuyPost, Long> {

    @Query(value = """
        SELECT * FROM "CoBuy_Post" p
        WHERE ST_DWithin(p.location, :center, :radius)
        AND p.status IN ('RECRUITING', 'FULL', 'PURCHASED', 'COMPLETED')
        ORDER BY ST_Distance(p.location, :center) ASC
        """, nativeQuery = true)
    List<CoBuyPost> findNearbyPosts(
            @Param("center") Point center,
            @Param("radius") double radiusMeters
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM CoBuyPost p WHERE p.postId = :postId")
    Optional<CoBuyPost> findByIdWithLock(@Param("postId") Long postId);

    // 1. 내가 방장인 글 조회 (최신순 정렬)
    List<CoBuyPost> findAllByHostUserOrderByCreatedAtDesc(User hostUser);

    // 2. 내가 참여한 글 조회 (조인 쿼리 필요)
    // CoBuyParticipant 테이블을 거쳐서 내가 참여한 Post만 가져옴.
    @Query("SELECT p.post FROM CoBuyParticipant p WHERE p.participantUser = :user ORDER BY p.post.createdAt DESC")
    List<CoBuyPost> findAllParticipatedPosts(@Param("user") User user);
}