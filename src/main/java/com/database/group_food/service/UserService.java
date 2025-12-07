package com.database.group_food.service;

import com.database.group_food.domain.User;
import com.database.group_food.dto.request.RegisterRequestDto;
import com.database.group_food.dto.response.UserResponseDto;
import com.database.group_food.repository.UserRepository;
import com.database.group_food.util.GeometryUtil;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeometryUtil geometryUtil;


     // 로그인 시 사용 (loadUserByUsername) 닉네임(username)으로 사용자를 DB에서 조회
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        // findByNickname이 Optional<User>를 반환,
        // User는 UserDetails를 구현했으므로 바로 반환 가능
        return userRepository.findByNickname(nickname)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with nickname: " + nickname)
                );
    }

    // 회원가입 시 사용 (register), DTO를 받아 사용자를 생성하고 저장
    @Transactional
    public User register(RegisterRequestDto requestDto) {

        // 1. 닉네임 중복 검사
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            // TODO: 더 구체적인 커스텀 예외로 변경하면 좋습니다.
            throw new IllegalStateException("Nickname is already taken.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 3. (경도, 위도) -> JTS Point 객체 변환
        Point location = geometryUtil.createPoint(requestDto.getLongitude(), requestDto.getLatitude());

        // 4. User 엔티티 생성
        User newUser = new User(
                requestDto.getNickname(),
                encodedPassword,
                location
        );

        // 5. DB에 저장
        return userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserResponseDto(user);
    }

    // 2. 위치 정보 업데이트
    @Transactional
    public void updateLocation(Long userId, double longitude, double latitude) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 새로운 Point 객체 생성 및 변경 (Dirty Checking으로 자동 저장)
        Point newLocation = geometryUtil.createPoint(longitude, latitude);
        user.setLocation(newLocation);
    }
}