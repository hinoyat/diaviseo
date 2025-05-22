package com.s206.user.user.dto.request;

import com.s206.user.user.enums.Goal;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    private String nickname;
    private String phone;
    private Goal goal;
    private BigDecimal height;
    private BigDecimal weight;
    private LocalDate birthday;
    private Boolean notificationEnabled;
}
