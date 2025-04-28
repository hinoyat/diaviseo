package com.s206.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.s206.auth.client.UserServiceClient;
import com.s206.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserServiceClient userServiceClient;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환용

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();
        String name = oAuth2User.getName();
        String provider = oAuth2User.getProvider();

        log.info("로그인 성공 - email: {}, name: {} provider: {}", email, name, provider);

        boolean exists = userServiceClient.checkUserExists(email, provider);

        log.info("exists: {}", exists);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        if (exists) {
            // 기존 회원 -> JWT 발급
            String token = jwtProvider.createToken(email, provider);

            Map<String, Object> successResult = new HashMap<>();
            successResult.put("isNewUser", false);
            successResult.put("token", token);

            String jsonResponse = objectMapper.writeValueAsString(successResult);
            response.getWriter().write(jsonResponse);
        } else {
            // 신규 회원 -> isNewUser: true만 응답
            Map<String, Object> newUserResult = new HashMap<>();
            newUserResult.put("isNewUser", true);

            String jsonResponse = objectMapper.writeValueAsString(newUserResult);
            response.getWriter().write(jsonResponse);
        }
    }
}
