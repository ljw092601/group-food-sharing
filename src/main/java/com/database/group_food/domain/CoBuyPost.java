package com.database.group_food.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.locationtech.jts.geom.Point;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@DynamicInsert
@Getter
@Setter
@Table(name = "\"CoBuy_Post\"")
public class CoBuyPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    // N(Post) : 1(User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id", nullable = false)
    private User hostUser;

    @Column(nullable = false, length = 100)
    private String itemName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int totalUnits;

    @Column(columnDefinition = "int default 1")
    private int currentUnits;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(nullable = false, columnDefinition = "geography(Point)")
    private Point location;

    @Column(length = 512)
    private String receiptImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private CoBuyStatus status = CoBuyStatus.RECRUITING;

    @CreationTimestamp // INSERT 시 자동 시간 입력
    private LocalDateTime createdAt;

    // 1(Post) : N(Participant)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL) // 글 삭제 시 참여자 목록도 삭제
    private List<CoBuyParticipant> participants;

    // 1(Post) : N(Review)
    @OneToMany(mappedBy = "post")
    private List<TransactionReview> reviews;

    @Column(length = 2048) // URL은 길 수 있으므로 넉넉하게
    private String purchaseUrl;
}
