package com.s206.user.physical.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.user.physical.dto.request.UserPhysicalInfoSaveRequest;
import com.s206.user.physical.dto.response.UserPhysicalInfoResponse;
import com.s206.user.physical.service.UserPhysicalInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users/physical-info")
@RequiredArgsConstructor
@Slf4j
public class UserPhysicalInfoController {

    private final UserPhysicalInfoService physicalInfoService;

    @PostMapping
    public ResponseEntity<ResponseDto<Void>> savePhysicalInfo(
            @RequestHeader("X-USER-ID") Integer userId,
            @RequestBody UserPhysicalInfoSaveRequest request
    ) {
        log.info("[POST] physical info - userId={}, date={}", userId, request.getDate());
        physicalInfoService.saveOrUpdate(userId, request);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "신체 정보 저장 성공"));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<UserPhysicalInfoResponse>> getPhysicalInfoByDate(
            @RequestHeader("X-USER-ID") Integer userId,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date == null) {
            log.info("[GET] latest physical info (default today) - userId={}", userId);
            date = LocalDate.now();
        } else {
            log.info("[GET] latest physical info - userId={}, date={}", userId, date);
        }
        log.info("[GET] latest physical info - userId={}, date={}", userId, date);
        UserPhysicalInfoResponse response = physicalInfoService.getLatestBeforeDate(userId, date);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "신체 정보 조회 성공", response));
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDto<List<UserPhysicalInfoResponse>>> getAllPhysicalInfo(
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        log.info("[GET] all physical info - userId={}", userId);
        List<UserPhysicalInfoResponse> responseList = physicalInfoService.getAll(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "신체 정보 전체 조회 성공", responseList));
    }
}
