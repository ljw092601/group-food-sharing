// src/main/java/com/database/group_food/security/JwtTokenProvider.java
package com.database.group_food.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 1. 비밀 키 (실무에서는 application.properties에 숨겨야 함)
    // 32글자 이상이어야 합니다.
    private final String SECRET_KEY = "my-super-secure-secret-key-for-group-food-project-2025";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    // 2. 토큰 유효 시간 (1일 = 24시간)
    private final long EXPIRATION_MS = 86400000;

    // [토큰 생성]
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // [토큰에서 닉네임 추출]
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // [토큰 유효성 검사]
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // 만료되었거나 위조된 토큰
            return false;
        }
    }
}