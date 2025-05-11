package com.s206.health.condition.allergy.service;

import com.s206.common.exception.types.NotFoundException;
import com.s206.health.condition.allergy.dto.response.FoodAllergyResponse;
import com.s206.health.condition.allergy.dto.response.UserAllergyResponse;
import com.s206.health.condition.allergy.dto.response.UserAllergyToggleResponse;
import com.s206.health.condition.allergy.entity.FoodAllergy;
import com.s206.health.condition.allergy.entity.UserAllergy;
import com.s206.health.condition.allergy.repository.FoodAllergyRepository;
import com.s206.health.condition.allergy.repository.UserAllergyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAllergyService {

    private final UserAllergyRepository userAllergyRepository;
    private final FoodAllergyRepository foodAllergyRepository;

    @Transactional
    public UserAllergyToggleResponse toggleAllergy(Integer userId, Long allergyId) {
        UserAllergy existing = userAllergyRepository.findByUserIdAndAllergyId(userId, allergyId).orElse(null);

        if (existing != null) {
            userAllergyRepository.delete(existing);
            return UserAllergyToggleResponse.toDto(existing, false);
        }

        FoodAllergy allergy = foodAllergyRepository.findById(allergyId)
                .orElseThrow(() -> new NotFoundException("해당 알러지를 찾을 수 없습니다."));

        UserAllergy newAllergy = userAllergyRepository.save(UserAllergy.builder()
                .userId(userId)
                .allergy(allergy)
                .build());

        return UserAllergyToggleResponse.toDto(newAllergy, true);
    }

    @Transactional(readOnly = true)
    public List<UserAllergyResponse> getUserAllergies(Integer userId) {
        return userAllergyRepository.findAllByUserId(userId).stream()
                .map(UserAllergyResponse::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FoodAllergyResponse> getAllAllergies() {
        return foodAllergyRepository.findAll().stream()
                .map(FoodAllergyResponse::toDto)
                .collect(Collectors.toList());
    }
}
