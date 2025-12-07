package com.database.group_food.repository;

import com.database.group_food.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 닉네임으로 사용자를 찾는 메서드
    Optional<User> findByNickname(String nickname);

    // 닉네임이 이미 존재하는지 확인하는 메서드
    boolean existsByNickname(String nickname);
}