package com.database.group_food.domain;

// CoBuyParticipant.java
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"CoBuy_Participant\"",
        uniqueConstraints = {
                // SQL의 UNIQUE (post_id, participant_user_id) 구현
                @UniqueConstraint(columnNames = {"post_id", "participant_user_id"})
        }
)
@Getter
@Setter
public class CoBuyParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_id")
    private Long participationId;

    // N(Participant) : 1(Post)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CoBuyPost post;

    // N(Participant) : 1(User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_user_id", nullable = false)
    private User participantUser;
}