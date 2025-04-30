package com.s206.auth.service;

import com.s206.auth.client.UserServiceClient;
import com.s206.auth.client.dto.UserExistResponse;
import com.s206.auth.dto.request.OAuthLoginRequest;
import com.s206.auth.dto.request.TestLoginRequest;
import com.s206.auth.dto.response.OAuthLoginResponse;
import com.s206.auth.jwt.JwtProvider;
import com.s206.auth.jwt.blacklist.TokenBlacklistService;
import com.s206.auth.jwt.refreshtoken.RefreshTokenRedisRepository;
import com.s206.auth.dto.response.ReissueResponse;
import com.s206.auth.security.google.GoogleTokenVerifier;
import com.s206.common.exception.types.BadRequestException;
import com.s206.common.exception.types.UnauthorizedException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserServiceClient userServiceClient;
    private final GoogleTokenVerifier googleTokenVerifier;


    public OAuthLoginResponse oauthLogin(OAuthLoginRequest request) {
        String provider = request.getProvider();
        String idToken = request.getIdToken();

        // 1. 소셜 토큰 검증
        String email;
        String name;
        if ("google".equalsIgnoreCase(provider)) {
            var userInfo = googleTokenVerifier.verify(idToken);
            email = userInfo.getEmail();
            name = userInfo.getName();
        } else {
            throw new UnauthorizedException("지원하지 않는 소셜 로그인 방식입니다: " + provider);
        }

        // 2. user-service 회원 존재 여부 조회
        UserExistResponse userExistResponse = userServiceClient.getUserInfo(email, provider);

        // 3. 회원 존재 여부에 따라 처리
        if (userExistResponse.getExists()) {

            // 기존 리프레쉬 토큰 삭제
            Integer userId = userExistResponse.getUserId();
            refreshTokenRedisRepository.deleteAllByUserId(userId);

            // 기존 회원 - JWT 발급
            String accessToken = jwtProvider.createAccessToken(
                    userExistResponse.getUserId(),
                    userExistResponse.getName(),
                    List.of("ROLE_USER")
            );
            String refreshToken = jwtProvider.createRefreshToken(
                    userExistResponse.getUserId(),
                    userExistResponse.getName()
            );

            // 기존 회원
            return new OAuthLoginResponse(false, accessToken, refreshToken);
        } else {
            // 신규 회원
            return new OAuthLoginResponse(true, null, null);
        }
    }


    public OAuthLoginResponse testLogin(TestLoginRequest request) {

        String email = request.getEmail();
        String provider = request.getProvider();

        UserExistResponse userExistResponse = userServiceClient.getUserInfo(email, provider);


        // 3. 회원 존재 여부에 따라 처리
        if (userExistResponse.getExists()) {
            
            // 기존 리프레쉬 토큰 삭제
            Integer userId = userExistResponse.getUserId();
            refreshTokenRedisRepository.deleteAllByUserId(userId);
            
            // 기존 회원 - JWT 발급
            String accessToken = jwtProvider.createAccessToken(
                    userExistResponse.getUserId(),
                    userExistResponse.getName(),
                    List.of("ROLE_USER")
            );
            String refreshToken = jwtProvider.createRefreshToken(
                    userExistResponse.getUserId(),
                    userExistResponse.getName()
            );

            // 기존 회원
            return new OAuthLoginResponse(false, accessToken, refreshToken);
        } else {
            // 신규 회원
            return new OAuthLoginResponse(true, null, null);
        }
    }


    @Transactional
    public ReissueResponse reissue(String authorizationHeader) {
        String refreshToken = extractToken(authorizationHeader);

        // 리프레시 토큰 유효성 검증
        Map<Object, Object> valueMap = validateRefreshToken(refreshToken);

        Integer userId = Integer.parseInt((String) valueMap.get("userId"));
        String name = (String) valueMap.get("name");

        // 리프레시 토큰 타입 검증
        String tokenType = jwtProvider.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new BadRequestException("Refresh Token이 아닙니다.");
        }

        // 기존 Refresh Token 삭제
        refreshTokenRedisRepository.deleteAllByUserId(userId);
        log.info("[Reissue] 기존 Refresh Token 삭제 완료");

        // 새 Access Token 발급
        String accessToken = jwtProvider.createAccessToken(
                userId,
                name,
                List.of("ROLE_USER")
        );

        // 새 Refresh Token 발급
        String newRefreshToken = jwtProvider.createRefreshToken(
                userId,
                name
        );
        log.info("[Reissue] 새 Refresh Token 발급 완료");

        return ReissueResponse.toDto(accessToken, newRefreshToken);
    }


    @Transactional
    public void logout(String accessTokenHeader, String refreshTokenHeader) {
        // 헤더에서 토큰 추출 Bearer 제거
        String accessToken = extractToken(accessTokenHeader);
        String refreshToken = extractToken(refreshTokenHeader);

        // Refresh Token 검증
        validateRefreshToken(refreshToken);
        log.info("[Logout] Refresh Token 유효성 검증 완료");

        // Access Token 블랙리스트 등록
        Claims claims = jwtProvider.parseClaims(accessToken);
        tokenBlacklistService.addToBlacklist(accessToken, claims.getExpiration());
        log.info("[Logout] Access Token 블랙리스트 등록 완료");

        // Refresh Token 삭제
        refreshTokenRedisRepository.delete(refreshToken);
        log.info("[Logout] Refresh Token 삭제 완료");
    }

    private Map<Object, Object> validateRefreshToken(String refreshToken) {
        Map<Object, Object> valueMap = refreshTokenRedisRepository.findByRefreshToken(refreshToken);
        if (valueMap == null || valueMap.isEmpty()) {
            throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다.");
        }
        return valueMap;
    }

    private String extractToken(String header) {
        if (header == null || header.isBlank()) {
            throw new UnauthorizedException("Authorization 헤더가 비어 있습니다.");
        }
        if (header.startsWith("Bearer ")) {
            return header.substring(7); // Bearer 제거
        } else {
            return header;
        }
    }
}
