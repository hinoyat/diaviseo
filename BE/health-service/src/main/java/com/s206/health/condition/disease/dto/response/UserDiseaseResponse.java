package com.s206.health.condition.disease.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.s206.health.condition.disease.entity.UserDisease;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDiseaseResponse {
    private Long diseaseId;
    private String diseaseName;

    private final boolean isRegistered = true;

    @JsonProperty("isRegistered")
    public boolean getIsRegistered() {
        return isRegistered;
    }

    public static UserDiseaseResponse toDto(UserDisease userDisease) {
        return UserDiseaseResponse.builder()
                .diseaseId(userDisease.getDisease().getId())
                .diseaseName(userDisease.getDisease().getName())
                .build();
    }
}
