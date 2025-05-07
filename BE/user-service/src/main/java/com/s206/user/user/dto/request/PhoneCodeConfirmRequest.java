package com.s206.user.user.dto.request;

import lombok.Getter;

@Getter
public class PhoneCodeConfirmRequest {
    private String phone;
    private String code;
}
