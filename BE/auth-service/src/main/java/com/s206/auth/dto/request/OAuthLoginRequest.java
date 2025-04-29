package com.s206.auth.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthLoginRequest {

    private String provider;
    private String idToken;
}
