package com.s206.auth.jwt;

import com.s206.auth.jwt.refreshtoken.RefreshTokenRedisRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenValidity;

    private Key key;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @PostConstruct
    public void init() {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Access Token 발급
    public String createAccessToken(Integer userId, String name, List<String> roles) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId); // 유저 ID
        claims.put("name", name); // 유저 이름
        claims.put("roles", roles); // 유저 권한
        claims.put("tokenType", "access"); // 추가: 토큰 타입 지정

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 발급
    public String createRefreshToken(Integer userId, String name) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("name", name);
        claims.put("tokenType", "refresh"); // 추가: 토큰 타입 지정

        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidity);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        refreshTokenRedisRepository.save(refreshToken, userId, name, refreshTokenValidity);

        return refreshToken;
    }

    // 토큰 클레임 파싱
    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 토큰 타입 가져오기
    public String getTokenType(String token) {
        Claims claims = parseClaims(token);
        return (String) claims.get("tokenType");
    }
}
