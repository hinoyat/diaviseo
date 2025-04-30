package com.s206.health.bodyinfo.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.bodyinfo.dto.request.BodyInfoCreateRequest;
import com.s206.health.bodyinfo.dto.response.BodyInfoResponse;
import com.s206.health.bodyinfo.service.BodyInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bodies")
public class BodyInfoController {
	private final BodyInfoService bodyInfoService;
	// @RequestBody : HTTP(JSON, XML 등) 요청 본문을 자바 객체로 변환
	// @Valid 요청 데이터의 유효성을 검사, 검사 실패하면 MethodArgumentNotValidException 발생, 400 Bad Request 반환.
	// TODO : 올바른 UserID인지 검증하기
	@PostMapping("/{userId}")
	public ResponseEntity<ResponseDto<BodyInfoResponse>> create(@PathVariable Integer userId, @Valid @RequestBody
			BodyInfoCreateRequest request){
		BodyInfoResponse response = bodyInfoService.create(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success(HttpStatus.CREATED, "체성분 데이터 등록 성공", response));
	}
}
