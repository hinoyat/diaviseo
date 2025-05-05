package com.s206.health.exercise.favorite.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.exercise.favorite.dto.response.FavoriteExerciseResponse;
import com.s206.health.exercise.favorite.dto.response.FavoriteToggleResponse;
import com.s206.health.exercise.favorite.service.FavoriteExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises/favorite")
@RequiredArgsConstructor
@Slf4j
public class FavoriteExerciseController {

    private final FavoriteExerciseService favoriteExerciseService;

    // 운동 즐겨찾기 토글 (등록 / 해제)
    @PostMapping("/{exerciseTypeId}")
    public ResponseEntity<ResponseDto<FavoriteToggleResponse>> toggleFavorite(
            @RequestHeader("X-USER-ID") Integer userId,
            @PathVariable Integer exerciseTypeId) {

        FavoriteToggleResponse response = favoriteExerciseService.toggleFavorite(userId, exerciseTypeId);

        return ResponseEntity.ok(
                ResponseDto.success(HttpStatus.OK, "즐겨찾기 변경 완료", response)
        );
    }
    
    // 즐겨찾기한 운동 목록 조회
    @GetMapping
    public ResponseEntity<ResponseDto<List<FavoriteExerciseResponse>>> getFavorites(
            @RequestHeader("X-USER-ID") Integer userId) {

        List<FavoriteExerciseResponse> response = favoriteExerciseService.getFavoriteExercises(userId);

        return ResponseEntity.ok(
                ResponseDto.success(HttpStatus.OK, "즐겨찾기 목록 조회 성공", response)
        );
    }
}
