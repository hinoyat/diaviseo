package com.s206.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthLoginResponse {
    private boolean isNewUser;
    private String accessToken;
    private String refreshToken;
}
