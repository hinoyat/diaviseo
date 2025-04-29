package com.s206.auth.security.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.s206.common.exception.types.UnauthorizedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class GoogleTokenVerifier {

    private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo";

    private final WebClient.Builder externalWebClientBuilder;

    public GoogleUserInfo verify(String idToken) {
        try {
            return externalWebClientBuilder.build()
                    .get()
                    .uri(GOOGLE_TOKEN_INFO_URL + "?id_token=" + idToken)
                    .retrieve()
                    .bodyToMono(GoogleUserInfo.class)
                    .block(); // 결과 대기
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
