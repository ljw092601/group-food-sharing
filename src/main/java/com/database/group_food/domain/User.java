package com.database.group_food.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.locationtech.jts.geom.Point;
// Spring Security UserDetails 임포트
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor // JPA를 위한 기본 생성자
@DynamicInsert
@Table(name = "\"User\"")
public class User implements UserDetails { // UserDetails 구현

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // ... (기존: nickname, passwordHash, location, trustScore 등) ...
    @Column(unique = true, nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, columnDefinition = "geography(Point)")
    private Point location;

    @Column(name = "trust_score", columnDefinition = "float default 50.0")
    private Double trustScore;

    @Column(name = "is_phone_verified", columnDefinition = "boolean default false")
    private boolean isPhoneVerified;

    // --- 연관관계 매핑 (엔티티 코드에서 가져옴) ---
    @OneToMany(mappedBy = "hostUser")
    private List<CoBuyPost> hostedPosts;

    @OneToMany(mappedBy = "participantUser")
    private List<CoBuyParticipant> participations;

    // ... (reviewsWritten, reviewsReceived) ...


    // --- 회원가입을 위한 생성자 (Service에서 사용) ---
    public User(String nickname, String passwordHash, Point location) {
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.location = location;
        // trustScore, isPhoneVerified 등은 @DynamicInsert로 인해 default 값 자동 적용
    }


    // --- UserDetails 인터페이스 메서드 구현 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 이 프로젝트에서는 별도 권한이 없으므로, 기본 "ROLE_USER" 권한을 부여합니다.
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // Spring Security가 사용할 비밀번호 (DB에 저장된 해시)
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        // Spring Security가 사용할 사용자 식별자 (우리는 nickname 사용)
        return this.nickname;
    }

    // --- 계정 상태 관련 (전부 true로 설정) ---

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 안 됨
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명(비밀번호) 만료 안 됨
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화됨
    }
}