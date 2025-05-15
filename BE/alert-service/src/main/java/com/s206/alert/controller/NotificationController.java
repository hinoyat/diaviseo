package com.s206.alert.controller;

import com.s206.alert.data.response.NotificationResponse;
import com.s206.alert.service.NotificationService;
import com.s206.common.dto.ResponseDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alert")
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	public ResponseEntity<ResponseDto<Page<NotificationResponse>>> getNotifications(
			@RequestHeader(name = "X-USER-ID") Integer userId,
			@PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ResponseEntity.ok(ResponseDto.<Page<NotificationResponse>>builder()
				.data(notificationService.getUserNotifications(userId, pageable))
				.message("알림 목록 조회 성공").status(
						HttpStatus.OK).timestamp(LocalDateTime.now()).build());
	}

	@PatchMapping("/{notificationId}/read")
	public ResponseEntity<ResponseDto<Void>> markAsRead(
			@RequestHeader(name = "X-USER-ID") Integer userId,
			@PathVariable(name = "notificationId") Long notificationId
	) {
		notificationService.markAsRead(userId, notificationId);
		return ResponseEntity.ok(ResponseDto.<Void>builder()
				.status(HttpStatus.OK)
				.message("읽음 처리 됐습니다")
				.timestamp(LocalDateTime.now())
				.build());
	}

	@PatchMapping("/read-all")
	public ResponseEntity<ResponseDto<Void>> markAllAsRead(
			@RequestHeader(name = "X-USER-ID") Integer userId
	) {
		notificationService.markAllAsRead(userId);
		return ResponseEntity.ok(ResponseDto.<Void>builder()
				.status(HttpStatus.OK)
				.message("읽음 처리 됐습니다")
				.timestamp(LocalDateTime.now())
				.build());
	}
}
