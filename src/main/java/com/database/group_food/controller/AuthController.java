// src/main/java/com/database/group_food/controller/AuthController.java
package com.database.group_food.controller;

import com.database.group_food.domain.User;
import com.database.group_food.dto.request.LoginRequestDto;
import com.database.group_food.dto.request.RegisterRequestDto;
import com.database.group_food.service.UserService;
import com.database.group_food.dto.request.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.database.group_food.security.JwtTokenProvider; // 임포트
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    // TODO: private final JwtTokenProvider jwtTokenProvider; (JWT 사용 시)

    /**
     * 회원가입 (Sign-up)
     * @param requestDto (nickname, password, location)
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto requestDto) {
        try {
            userService.register(requestDto);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalStateException e) {
            // 닉네임 중복 등
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외
            return ResponseEntity.status(500).body("An error occurred during registration.");
        }
    }

    /**
     * 로그인 (Sign-in)
     * @param requestDto (nickname, password)
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto requestDto) {
        try {
            // 1. 아이디/비번 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getNickname(),
                            requestDto.getPassword()
                    )
            );

            // 2. 인증 정보를 Context에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. [핵심] JWT 토큰 생성
            String jwt = jwtTokenProvider.generateToken(authentication);

            // 4. 토큰 반환 (JSON 형식으로 주는 것이 깔끔함)
            return ResponseEntity.ok(Map.of("accessToken", jwt));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Login failed: " + e.getMessage());
        }
    }
}