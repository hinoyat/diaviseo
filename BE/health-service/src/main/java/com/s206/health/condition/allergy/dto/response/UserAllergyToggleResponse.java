package com.s206.health.condition.allergy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.s206.health.condition.allergy.entity.UserAllergy;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAllergyToggleResponse {
    private Long allergyId;
    private boolean isRegistered;
    private String message;

    @JsonProperty("isRegistered")
    public boolean getIsRegistered() {
        return isRegistered;
    }

    public static UserAllergyToggleResponse toDto(UserAllergy userAllergy, boolean isNowRegistered) {
        return UserAllergyToggleResponse.builder()
                .allergyId(userAllergy.getAllergy().getId())
                .isRegistered(isNowRegistered)
                .message(isNowRegistered ? "알러지 등록 완료" : "알러지 해제 완료")
                .build();
    }
}
