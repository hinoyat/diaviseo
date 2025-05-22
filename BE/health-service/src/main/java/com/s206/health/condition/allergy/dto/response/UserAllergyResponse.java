package com.s206.health.condition.allergy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.s206.health.condition.allergy.entity.UserAllergy;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAllergyResponse {
    private Long allergyId;
    private String allergyName;

    private final boolean isRegistered = true;

    @JsonProperty("isRegistered")
    public boolean getIsRegistered() {
        return isRegistered;
    }

    public static UserAllergyResponse toDto(UserAllergy userAllergy) {
        return UserAllergyResponse.builder()
                .allergyId(userAllergy.getAllergy().getId())
                .allergyName(userAllergy.getAllergy().getName())
                .build();
    }
}

