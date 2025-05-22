package com.s206.user.user.dto.response;

import com.s206.user.user.entity.User;
import com.s206.user.user.enums.Gender;
import com.s206.user.user.enums.Goal;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserDetailResponse {

    private String name;

    private String nickname;

    private Gender gender;

    private BigDecimal height;

    private BigDecimal weight;

    private Goal goal;

    private LocalDate birthday;

    private String phone;

    private String email;

    private Boolean consentPersonal;

    private Boolean locationPersonal;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private Boolean isDeleted;

    private Boolean notificationEnabled;



    public static UserDetailResponse toDto(User user) {
        return UserDetailResponse.builder()
                .name(user.getName())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .height(user.getHeight())
                .weight(user.getWeight())
                .goal(user.getGoal())
                .birthday(user.getBirthday())
                .phone(user.getPhone())
                .email(user.getEmail())
                .consentPersonal(user.getConsentPersonal())
                .locationPersonal(user.getLocationPersonal())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .isDeleted(user.getIsDeleted())
                .notificationEnabled(user.getNotificationEnabled())
                .build();

    }
}
