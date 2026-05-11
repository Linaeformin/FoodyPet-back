package org.example.foodypet.domain.user.service;

import io.jsonwebtoken.JwtException;
import org.example.foodypet.common.config.CustomUserDetails;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.JwtProvider;
import org.example.foodypet.domain.user.dto.*;
import org.example.foodypet.domain.user.entity.User;
import org.example.foodypet.domain.user.repository.RefreshTokenRepository;
import org.example.foodypet.domain.user.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;


// 회원가입, 로그인 서비스
@Service
@RequiredArgsConstructor
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public void signup(SignupFormDto signupFormDto) {

        if (usersRepository.existsByUserId(signupFormDto.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        String randomNickname = createRandomNickname();

        User user = User.builder()
                .userId(signupFormDto.getUserId())
                .password(passwordEncoder.encode(signupFormDto.getPassword()))
                .nickname(randomNickname)
                .build();

        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResDto login(LoginFormDto loginFormDto) {

        User user = usersRepository.findByUserId(loginFormDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(loginFormDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        refreshTokenRepository.save(user.getId(), refreshToken);

        return new LoginResDto(accessToken, refreshToken);
    }

    @Transactional
    public LoginResDto refresh(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "REFRESH_TOKEN_REQUIRED");
        }

        String userId;

        try {
            userId = jwtProvider.getSubject(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(UNAUTHORIZED, "INVALID_REFRESH_TOKEN");
        }

        User user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "USER_NOT_FOUND"));

        String savedRefreshToken = refreshTokenRepository.find(user.getId());

        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new ResponseStatusException(UNAUTHORIZED, "REFRESH_TOKEN_NOT_MATCHED");
        }

        String newAccessToken = jwtProvider.createAccessToken(user.getUserId());

        return new LoginResDto(newAccessToken, refreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "USER_NOT_FOUND"));

        refreshTokenRepository.delete(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = usersRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return new CustomUserDetails(user);
    }

    public DuplicateIdResDto checkDuplicateId(DuplicateIdFormDto formDto) {
        boolean isDuplicate = usersRepository.existsByUserId(formDto.getUserId());

        DuplicateIdResDto resDto = new DuplicateIdResDto();
        resDto.setUserId(formDto.getUserId());
        resDto.setDuplicate(isDuplicate);

        return resDto;
    }

    private String createRandomNickname() {
        String[] adjectives = {
                "귀여운", "행복한", "배고픈", "졸린", "신나는",
                "말랑한", "용감한", "차분한", "활발한", "따뜻한"
        };

        String[] names = {
                "강아지", "고양이", "토끼", "햄스터", "여우",
                "곰돌이", "다람쥐", "병아리", "수달", "판다"
        };

        int randomNumber = (int) (Math.random() * 10000);

        String adjective = adjectives[(int) (Math.random() * adjectives.length)];
        String name = names[(int) (Math.random() * names.length)];

        return adjective + name + randomNumber;
    }
}
