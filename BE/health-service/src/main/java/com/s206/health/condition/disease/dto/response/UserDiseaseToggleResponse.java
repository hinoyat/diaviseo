package com.s206.health.condition.disease.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.s206.health.condition.disease.entity.UserDisease;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDiseaseToggleResponse {
    private Long diseaseId;
    private boolean isRegistered;
    private String message;

    @JsonProperty("isRegistered")
    public boolean getIsRegistered() {
        return isRegistered;
    }

    public static UserDiseaseToggleResponse toDto(UserDisease userDisease, boolean isNowRegistered) {
        return UserDiseaseToggleResponse.builder()
                .diseaseId(userDisease.getDisease().getId())
                .isRegistered(isNowRegistered)
                .message(isNowRegistered ? "질환 등록 완료" : "질환 해제 완료")
                .build();
    }
}
