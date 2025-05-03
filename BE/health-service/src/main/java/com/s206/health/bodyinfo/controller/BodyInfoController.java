package com.s206.health.bodyinfo.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.bodyinfo.dto.request.BodyInfoCreateRequest;
import com.s206.health.bodyinfo.dto.response.BodyInfoResponse;
import com.s206.health.bodyinfo.service.BodyInfoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bodies")
public class BodyInfoController {
	private final BodyInfoService bodyInfoService;

	@PostMapping
	public ResponseEntity<ResponseDto<BodyInfoResponse>> create(@RequestHeader("X-USER-ID") Integer userId, @Valid @RequestBody
			BodyInfoCreateRequest request){
		BodyInfoResponse response = bodyInfoService.create(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success(HttpStatus.CREATED, "체성분 데이터 등록 성공", response));
	}


	@GetMapping
	public ResponseEntity<ResponseDto<List<BodyInfoResponse>>> findByUserId(@RequestHeader("X-USER-ID") Integer userId){
		List<BodyInfoResponse> response = bodyInfoService.findByUserId(userId);
		return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK, "유저 체성분 정보 목록 요청이 성공적으로 반환 처리됐습니다.", response));
	}
}
