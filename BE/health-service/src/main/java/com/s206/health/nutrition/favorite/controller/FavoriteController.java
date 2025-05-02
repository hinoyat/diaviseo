package com.s206.health.nutrition.favorite.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.nutrition.favorite.dto.response.FavoriteFoodResponse;
import com.s206.health.nutrition.favorite.dto.response.FavoriteToggleResponse;
import com.s206.health.nutrition.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods/favorites")
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{foodId}")
    public ResponseEntity<ResponseDto<FavoriteToggleResponse>> toggleFavorite(
            @RequestHeader("X-USER-ID") Integer userId,
            @PathVariable Integer foodId) {

        log.info("즐겨찾기 토글 요청 - userId={}, foodId={}", userId, foodId);
        FavoriteToggleResponse response = favoriteService.toggleFavorite(userId, foodId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "즐겨찾기 토글 성공", response));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<FavoriteFoodResponse>>> getFavorites(
            @RequestHeader("X-USER-ID") Integer userId) {

        log.info("즐겨찾기 목록 조회 요청 - userId={}", userId);
        List<FavoriteFoodResponse> response = favoriteService.getFavoriteFoods(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "즐겨찾기 목록 조회 성공", response));
    }
}