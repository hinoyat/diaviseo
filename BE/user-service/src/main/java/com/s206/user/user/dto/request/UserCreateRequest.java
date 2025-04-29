package com.s206.user.user.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserCreateRequest {

    private String name;

    private String nickname;

    private String gender;

    private LocalDate birthday;

    private String phone;

    private String email;

    private String provider;

    private Boolean consentPersonal;

    private Boolean locationPersonal;
}
