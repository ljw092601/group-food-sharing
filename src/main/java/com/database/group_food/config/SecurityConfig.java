package com.database.group_food.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.database.group_food.security.JwtAuthenticationFilter; // 임포트 추가
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // 임포트 추가

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 2. AuthenticationManager Bean 등록 (로그인 시 사용)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 3. SecurityFilterChain 설정 (HTTP 보안 규칙)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF, Form Login, HTTP Basic 비활성화 (API 서버이므로)
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 세션 관리: STATELESS (JWT 기반이므로 세션 사용 안 함)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 1. 웰컴 페이지와 헬스 체크 허용
                        .requestMatchers("/", "/api/health").permitAll()
                        // 2. 회원가입/로그인 허용
                        .requestMatchers("/api/auth/**").permitAll()
                        // 3. (선택) Swagger 문서 접근 허용 (나중에 추가 시)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // ▼▼▼ [추가] 프론트엔드 페이지 허용 (로그인, 회원가입 화면) ▼▼▼
                        .requestMatchers("/login", "/register").permitAll()
                        // ▼▼▼ [추가] favicon, error 페이지, css/js 폴더 허용 ▼▼▼
                        .requestMatchers("/favicon.ico", "/error", "/css/**", "/js/**").permitAll()
                        // ▼▼▼ [추가] 글쓰기 관련 페이지도 HTML은 열어줍니다! ▼▼▼
                        .requestMatchers("/post/**").permitAll()
                        // 4. 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}