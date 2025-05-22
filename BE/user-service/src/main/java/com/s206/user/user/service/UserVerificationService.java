package com.s206.user.user.service;

import com.s206.common.exception.types.AiCallFailedException;
import com.s206.common.exception.types.BadRequestException;
import com.s206.common.exception.types.InternalServerErrorException;
import com.s206.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.test.TestFailedException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVerificationService {

    private final SmsService smsService;

    private final UserRepository userRepository;

    private final RedisTemplate<String, String> redisTemplate;

    private static final long CODE_EXPIRE_SECONDS = 180; // 3분
    private static final String REDIS_PREFIX = "phone:";

    public void sendVerificationCode(String phone) {
        if (userRepository.existsByPhone(phone)) {
            throw new BadRequestException("이미 가입된 전화번호입니다.");
        }

        String code = generateRandomCode();
        String key = REDIS_PREFIX + phone;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            throw new BadRequestException("이미 인증 요청이 진행 중입니다. 잠시 후 다시 시도해주세요.");
        }

        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        log.info("인증번호 [{}]가 Redis에 저장됨. Key = {}", code, key);


        try {
            smsService.sendSms(phone, code);
        } catch (Exception e) {
            redisTemplate.delete(key); // 전송 실패 시 Redis 정리
            throw new InternalServerErrorException("SMS 인증번호 전송에 실패했습니다."); // → 커스텀 예외 or 500 응답
        }
    }
//    public String sendVerificationCode(String phone) {
//        if (userRepository.existsByPhone(phone)) {
//            throw new BadRequestException("이미 가입된 전화번호입니다.");
//        }
//
//        String code = generateRandomCode();
//        String key = REDIS_PREFIX + phone;
//
//        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
//            throw new BadRequestException("이미 인증 요청이 진행 중입니다. 잠시 후 다시 시도해주세요.");
//        }
//
//        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_SECONDS, TimeUnit.SECONDS);
//        log.info("인증번호 [{}]가 Redis에 저장됨. Key = {}", code, key);
//
//        return code;
//    }

    public void verifyCode(String phone, String code) {
        String key = REDIS_PREFIX + phone;
        String failKey = "phone:fail:" + phone;

        // 실패 횟수 검사
        String failCountStr = redisTemplate.opsForValue().get(failKey);
        int failCount = failCountStr != null ? Integer.parseInt(failCountStr) : 0;
        if (failCount >= 5) {
            throw new BadRequestException("인증 시도 횟수가 초과되었습니다. 잠시 후 다시 시도해주세요.");
        }

        String storedCode = redisTemplate.opsForValue().get(key);
        if (storedCode == null) {
            redisTemplate.opsForValue().increment(failKey);
            redisTemplate.expire(failKey, 3, TimeUnit.MINUTES); // 3분 동안 블록 유지
            throw new BadRequestException("인증번호가 만료되었거나 존재하지 않습니다.");
        }

        if (!storedCode.equals(code)) {
            redisTemplate.opsForValue().increment(failKey);
            redisTemplate.expire(failKey, 3, TimeUnit.MINUTES);
            throw new BadRequestException("인증번호가 일치하지 않습니다.");
        }

        // 인증 성공 시 Redis에서 인증번호 + 실패 기록 삭제
        redisTemplate.delete(key);
        redisTemplate.delete(failKey);
        log.info("인증 성공 - phone: {}", phone);
    }




    private String generateRandomCode() {
        int number = ThreadLocalRandom.current().nextInt(100_000, 1_000_000); // 6자리
        return String.valueOf(number);
    }
}
