package com.database.group_food.repository;

import com.database.group_food.domain.CoBuyParticipant;
import com.database.group_food.domain.CoBuyPost;
import com.database.group_food.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoBuyParticipantRepository extends JpaRepository<CoBuyParticipant, Long> {
    // 이미 참여했는지 확인
    boolean existsByPostAndParticipantUser(CoBuyPost post, User participantUser);
}