package com.s206.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserExistResponse {
    private Integer userId;
    private String name;
    private Boolean exists;
}
