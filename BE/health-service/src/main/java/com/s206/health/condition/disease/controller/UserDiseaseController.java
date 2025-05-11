package com.s206.health.condition.disease.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.condition.disease.dto.response.DiseaseResponse;
import com.s206.health.condition.disease.dto.response.UserDiseaseResponse;
import com.s206.health.condition.disease.dto.response.UserDiseaseToggleResponse;
import com.s206.health.condition.disease.service.UserDiseaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bodies/diseases")
@RequiredArgsConstructor
@Slf4j
public class UserDiseaseController {

    private final UserDiseaseService userDiseaseService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<DiseaseResponse>>> getAllDiseases() {
        List<DiseaseResponse> result = userDiseaseService.getAllDiseases();
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "기준 질환 목록 조회 완료", result));
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseDto<List<UserDiseaseResponse>>> getUserDiseases(
            @RequestHeader("X-USER-ID") Integer userId) {
        List<UserDiseaseResponse> result = userDiseaseService.getUserDiseases(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "내 질환 목록 조회 완료", result));
    }

    @PostMapping("/{diseaseId}/toggle")
    public ResponseEntity<ResponseDto<UserDiseaseToggleResponse>> toggleUserDisease(
            @RequestHeader("X-USER-ID") Integer userId,
            @PathVariable Long diseaseId) {
        UserDiseaseToggleResponse result = userDiseaseService.toggleDisease(userId, diseaseId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "질환 상태 변경 완료", result));
    }
}
