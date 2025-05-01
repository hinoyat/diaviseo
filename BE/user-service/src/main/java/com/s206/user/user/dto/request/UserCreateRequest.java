package com.s206.user.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@ToString
@Builder
public class UserCreateRequest {

    private String name;

    private String nickname;

    private String gender;

    private LocalDate birthday;

    private BigDecimal height;

    private BigDecimal weight;

    private String phone;

    private String email;

    private String provider;

    private Boolean consentPersonal;

    private Boolean locationPersonal;
}
