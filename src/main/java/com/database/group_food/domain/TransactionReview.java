package com.database.group_food.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"Transaction_Review\"",
        uniqueConstraints = {
                // SQL의 UNIQUE (post_id, reviewer_id, reviewee_id) 구현
                @UniqueConstraint(columnNames = {"post_id", "reviewer_id", "reviewee_id"})
        }
)
@Getter
@Setter
public class TransactionReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CoBuyPost post;

    // 평가를 "쓴" 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    // 평가를 "받은" 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
