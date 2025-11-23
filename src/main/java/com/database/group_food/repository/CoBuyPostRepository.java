// src/main/java/com/database/group_food/repository/CoBuyPostRepository.java
package com.database.group_food.repository;

import com.database.group_food.domain.CoBuyPost;
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
}