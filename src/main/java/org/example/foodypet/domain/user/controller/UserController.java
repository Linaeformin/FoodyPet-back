package org.example.foodypet.domain.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.ApiSuccess;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.common.config.JwtProvider;
import org.example.foodypet.domain.user.dto.*;
import org.example.foodypet.domain.user.service.UsersService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


// 회원가입, 로그인 로직
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UsersService usersService;

    @Value("${jwt.refresh-exp-millis}")
    private long refreshExpMillis;

    // 아이디 중복 확인
    @PostMapping("/duplicate")
    public ResponseEntity<?> duplicate(@Valid @RequestBody DuplicateIdFormDto duplicateIdFormDto) {
        return ResponseEntity.ok(usersService.checkDuplicateId(duplicateIdFormDto));
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupFormDto signupFormDto) {

        usersService.signup(signupFormDto);

        return ResponseEntity
                .status(201)
                .body(new ApiSuccess(201, "성공적으로 처리되었습니다."));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResDto> login(
            @Valid @RequestBody LoginFormDto loginFormDto,
            HttpServletResponse response
    ) {
        LoginResDto tokenResponse = usersService.login(loginFormDto);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokenResponse.getRefreshToken())
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(refreshExpMillis / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                .body(tokenResponse);
    }

    // refresh 토큰으로 accessToken 재발급
    @PostMapping("/refresh")
    public ResponseEntity<LoginResDto> refresh(
            @Valid @RequestBody RefreshFormDto refreshFormDto
            ) {
        LoginResDto tokenResponse = usersService.refresh(refreshFormDto.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                .body(tokenResponse);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @AuthenticationPrincipal CustomUserDetails me,
            HttpServletResponse response
    ) {
        usersService.logout(me.getId());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .status(200)
                .body(new ApiSuccess(200, "성공적으로 처리되었습니다."));
    }
}