package com.database.group_food.service;

import com.database.group_food.domain.CoBuyParticipant;
import com.database.group_food.domain.CoBuyPost;
import com.database.group_food.domain.CoBuyStatus;
import com.database.group_food.domain.User;
import com.database.group_food.repository.CoBuyParticipantRepository;
import com.database.group_food.repository.CoBuyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final CoBuyPostRepository postRepository;
    private final CoBuyParticipantRepository participantRepository;

    // 공동구매 참여하기 (핵심 트랜잭션)
    @Transactional
    public void joinPost(User user, Long postId) {

        // 1. 게시글 조회
        // -> 누군가 이미 수정 중이라면 여기서 대기하게 됨 (동시성 제어)
        CoBuyPost post = postRepository.findByIdWithLock(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // 2. 검증 로직
        // A. 이미 모집 완료되었는지 확인
        if (post.getStatus() != CoBuyStatus.RECRUITING) {
            throw new IllegalStateException("This post is not recruiting anymore.");
        }

        // B. 인원이 꽉 찼는지 확인 (락 덕분에 안전한 데이터)
        if (post.getCurrentUnits() >= post.getTotalUnits()) {
            throw new IllegalStateException("The party is full.");
        }

        // C. 이미 참여한 유저인지 확인
        if (participantRepository.existsByPostAndParticipantUser(post, user)) {
            throw new IllegalStateException("You already joined this post.");
        }

        // 3. 참여 처리 (비즈니스 로직)
        // A. 참여자 명단에 추가 (INSERT)
        CoBuyParticipant participant = new CoBuyParticipant();
        participant.setPost(post);
        participant.setParticipantUser(user);
        participantRepository.save(participant);

        // B. 현재 인원 증가 (UPDATE)
        post.setCurrentUnits(post.getCurrentUnits() + 1);

        // C. 만약 모집 인원이 다 찼다면 -> 상태를 FULL로 변경 (UPDATE)
        if (post.getCurrentUnits() == post.getTotalUnits()) {
            post.setStatus(CoBuyStatus.FULL);
        }

    }
}