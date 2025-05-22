package com.s206.auth.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestLoginRequest {
    private String email;
    private String provider;
}
