package com.s206.health.bodyinfo.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.bodyinfo.dto.request.BodyInfoCreateRequest;
import com.s206.health.bodyinfo.dto.request.BodyInfoPatchRequest;
import com.s206.health.bodyinfo.dto.response.BodyInfoProjection;
import com.s206.health.bodyinfo.dto.response.BodyInfoResponse;
import com.s206.health.bodyinfo.dto.response.WeeklyAverageBodyInfoResponse;
import com.s206.health.bodyinfo.service.BodyInfoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bodies")
public class BodyInfoController {

	private final BodyInfoService bodyInfoService;

	@PostMapping
	public ResponseEntity<ResponseDto<BodyInfoResponse>> create(
			@RequestHeader("X-USER-ID") Integer userId, @Valid @RequestBody
			BodyInfoCreateRequest request) {
		BodyInfoResponse response = bodyInfoService.create(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseDto.success(HttpStatus.CREATED, "체성분 데이터 등록 성공", response));
	}


	@GetMapping
	public ResponseEntity<ResponseDto<List<BodyInfoResponse>>> findByUserId(
			@RequestHeader("X-USER-ID") Integer userId) {
		List<BodyInfoResponse> response = bodyInfoService.findByUserId(userId);
		return ResponseEntity.ok(
				ResponseDto.success(HttpStatus.OK, "유저 체성분 정보 목록 요청이 성공적으로 반환 처리됐습니다.", response));
	}

	@PatchMapping("/{bodyId}")
	public ResponseEntity<ResponseDto<BodyInfoResponse>> patchBodyInfo(
			@RequestHeader("X-USER-ID") Integer userId,
			@PathVariable("bodyId") Integer bodyId,
			@Valid @RequestBody BodyInfoPatchRequest request
	) {
		BodyInfoResponse response = bodyInfoService.updateBodyInfo(userId, bodyId, request);
		return ResponseEntity.ok(
				ResponseDto.success(HttpStatus.OK, "체성분 정보가 성공적으로 수정되었습니다.", response));
	}

	@DeleteMapping("/{bodyId}")
	public ResponseEntity<ResponseDto<Void>> deleteBodyInfo(
			@RequestHeader("X-USER-ID") Integer userId,
			@PathVariable Integer bodyId) {
		bodyInfoService.deleteBodyInfo(userId, bodyId);
		return ResponseEntity.ok(
				ResponseDto.success(HttpStatus.OK, "체성분 정보가 성공적으로 삭제되었습니다.", null));
	}

	@GetMapping("/date")
	public ResponseEntity<ResponseDto<BodyInfoResponse>> findByDate(
			@RequestHeader("X-USER-ID") Integer userId,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) {

		if (date.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("미래 날짜는 입력할 수 없습니다.");
		}

		if (date.isBefore(LocalDate.of(2000, 1, 1))) {
			throw new IllegalArgumentException("2000년 1월 1일 이전 날짜는 입력할 수 없습니다.");
		}

		BodyInfoResponse response = bodyInfoService.findByUserIdAndDate(userId, date);
		return ResponseEntity.ok(
				ResponseDto.success(HttpStatus.OK, "유저 체성분 정보 조회가 성공적으로 처리됐습니다.", response));
	}

	@GetMapping("/weekly")
	public ResponseEntity<ResponseDto<List<BodyInfoProjection>>> getWeeklyBodyInfo(
			@RequestHeader("X-USER-ID") Integer userId,
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate endDate) {

		List<BodyInfoProjection> response = bodyInfoService.getWeeklyBodyInfo(userId, endDate);
		return ResponseEntity.ok(
				ResponseDto.success(HttpStatus.OK, "주간 체성분 정보 조회가 성공적으로 처리됐습니다.", response));
	}

	@GetMapping("/monthly")
	public ResponseEntity<ResponseDto<List<WeeklyAverageBodyInfoResponse>>> getMonthlyAverages(
			@RequestHeader("X-USER-ID") Integer userId,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate endDate
	) {
		List<WeeklyAverageBodyInfoResponse> response = bodyInfoService.getMonthlyBodyInfo(userId,
				endDate);

		return ResponseEntity.ok(
				ResponseDto.success(HttpStatus.OK, "월간 체성분 정보 조회가 성공적으로 처리됐습니다.", response));
	}
}
