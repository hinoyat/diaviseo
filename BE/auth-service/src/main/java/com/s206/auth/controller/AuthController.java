package com.s206.auth.controller;

import com.s206.auth.dto.request.OAuthLoginRequest;
import com.s206.auth.dto.request.TestLoginRequest;
import com.s206.auth.dto.response.OAuthLoginResponse;
import com.s206.auth.dto.response.ReissueResponse;
import com.s206.auth.service.AuthService;
import com.s206.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<ResponseDto<ReissueResponse>> reissue(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("[reissue] Authorization Header = {}", authorizationHeader);
        ReissueResponse reissueResponse = authService.reissue(authorizationHeader);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "토큰 재발급 성공", reissueResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<Void>> logout(
            @RequestHeader("Authorization") String accessTokenHeader,
            @RequestHeader("Refresh-Token") String refreshTokenHeader
    ) {
        log.info("[Logout] AccessToken {}, RefreshToken{}", accessTokenHeader, refreshTokenHeader);

        authService.logout(accessTokenHeader, refreshTokenHeader);

        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "로그아웃 성공", null));
    }

    @PostMapping("/oauth/login")
    public ResponseEntity<ResponseDto<OAuthLoginResponse>> login(@RequestBody OAuthLoginRequest request) {
        log.info("[OAuth Login] provider: {}, idToken: {}", request.getProvider(), request.getIdToken());
        OAuthLoginResponse response = authService.oauthLogin(request);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "소셜 로그인 성공", response));
    }

    @PostMapping("/test/login")
    public ResponseEntity<ResponseDto<OAuthLoginResponse>> testLogin(@RequestBody TestLoginRequest request) {
        log.info("[Test Login] provider: {}, idToken: {}", request.getEmail(), request.getProvider());
        OAuthLoginResponse response = authService.testLogin(request);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "테스트 로그인 성공", response));
    }


}
