package com.s206.health.condition.allergy.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.condition.allergy.dto.response.FoodAllergyResponse;
import com.s206.health.condition.allergy.dto.response.UserAllergyResponse;
import com.s206.health.condition.allergy.dto.response.UserAllergyToggleResponse;
import com.s206.health.condition.allergy.service.UserAllergyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bodies/allergies")
@RequiredArgsConstructor
@Slf4j
public class UserAllergyController {

    private final UserAllergyService userAllergyService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<FoodAllergyResponse>>> getAllAllergies() {
        List<FoodAllergyResponse> result = userAllergyService.getAllAllergies();
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "기준 알러지 목록 조회 완료", result));
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseDto<List<UserAllergyResponse>>> getUserAllergies(
            @RequestHeader("X-USER-ID") Integer userId) {
        List<UserAllergyResponse> result = userAllergyService.getUserAllergies(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "내 알러지 목록 조회 완료", result));
    }

    @PostMapping("/{allergyId}/toggle")
    public ResponseEntity<ResponseDto<UserAllergyToggleResponse>> toggleUserAllergy(
            @RequestHeader("X-USER-ID") Integer userId,
            @PathVariable Long allergyId) {
        UserAllergyToggleResponse result = userAllergyService.toggleAllergy(userId, allergyId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "알러지 상태 변경 완료", result));
    }
}
