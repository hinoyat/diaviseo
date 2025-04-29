package com.s206.auth.security.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.s206.common.exception.types.UnauthorizedException;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleTokenVerifier {

    private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    private final RestTemplate restTemplate = new RestTemplate();

    public GoogleUserInfo verify(String idToken) {
        try {
            GoogleUserInfo userInfo = restTemplate.getForObject(GOOGLE_TOKEN_INFO_URL + idToken, GoogleUserInfo.class);
            if (userInfo == null || userInfo.getEmail() == null) {
                throw new UnauthorizedException("구글 토큰 검증 실패");
            }
            return userInfo;
        } catch (Exception e) {
            throw new UnauthorizedException("구글 토큰 검증 실패");
        }
    }

    @Getter
    public static class GoogleUserInfo {
        @JsonProperty("email")
        private String email;

        @JsonProperty("name")
        private String name;
    }
}
