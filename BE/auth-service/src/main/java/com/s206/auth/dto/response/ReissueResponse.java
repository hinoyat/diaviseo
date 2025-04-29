package com.s206.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReissueResponse {
    private String accessToken;
    private String refreshToken;

    public static ReissueResponse toDto(String accessToken, String refreshToken) {
        return new ReissueResponse(accessToken, refreshToken);
    }
}
