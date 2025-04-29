package com.s206.user.user.dto.response;

import com.s206.user.user.entity.User;
import lombok.*;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserExistResponse {
    private Integer userId;
    private String name;
    private Boolean exists;

    // User 있는 경우 사용
    public static UserExistResponse toDto(User user, Boolean exists) {
        return UserExistResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .exists(exists)
                .build();
    }

    // User 없는 경우 사용
    public static UserExistResponse toDto(Boolean exists) {
        return UserExistResponse.builder()
                .userId(null)
                .name(null)
                .exists(exists)
                .build();
    }
}
