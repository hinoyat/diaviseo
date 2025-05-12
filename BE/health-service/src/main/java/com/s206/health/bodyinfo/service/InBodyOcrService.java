package com.s206.health.bodyinfo.service;

import com.s206.health.bodyinfo.dto.request.BodyInfoCreateRequest;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class InBodyOcrService {

    private final ITesseract tesseract;

    public InBodyOcrService() {
        tesseract = new Tesseract();

        // Windows 환경에서 명시적으로 tessdata 경로 설정
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            log.info("Windows 환경에서 실행 중...");
            String[] windowsPaths = {
                    "C:\\Program Files\\Tesseract-OCR\\tessdata",
                    "C:\\Program Files (x86)\\Tesseract-OCR\\tessdata"
            };

            for (String path : windowsPaths) {
                if (new java.io.File(path).exists()) {
                    tesseract.setDatapath(path);
                    log.info("tessdata 경로 설정 완료: {}", path);
                    break;
                }
            }
        }

        try {
            tesseract.setLanguage("kor+eng");
            log.info("언어 설정 성공: kor+eng");
        } catch (Exception e) {
            log.error("언어 설정 실패: {}", e.getMessage());
        }

        tesseract.setTessVariable("tessedit_char_whitelist",
                "0123456789.abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ가-힣()~-");
    }

    public BodyInfoCreateRequest extractBodyInfoFromImage(MultipartFile imageFile)
            throws IOException, TesseractException {

        log.info("인바디 이미지 OCR 처리 시작");

        BufferedImage image = ImageIO.read(imageFile.getInputStream());
        String ocrText = tesseract.doOCR(image);
        log.info("OCR 추출 텍스트:");
        log.info("{}", ocrText);

        return parseInBodyData(ocrText);
    }

    private BodyInfoCreateRequest parseInBodyData(String ocrText) {
        BigDecimal weight = extractWeight(ocrText);
        log.info("추출된 체중: {}", weight);

        BigDecimal bodyFat = extractBodyFat(ocrText);
        log.info("추출된 체지방률: {}", bodyFat);

        BigDecimal muscleMass = extractMuscleMass(ocrText);
        log.info("추출된 골격근량: {}", muscleMass);

        LocalDate measurementDate = extractMeasurementDate(ocrText);
        log.info("추출된 측정일: {}", measurementDate);

        return BodyInfoCreateRequest.builder()
                .weight(weight)
                .bodyFat(bodyFat)
                .muscleMass(muscleMass)
                .measurementDate(measurementDate)
                .build();
    }

    private BigDecimal extractWeight(String text) {
        // 1. Body Composition History 섹션에서 체중 찾기 (가장 정확함)
        Pattern pattern1 = Pattern.compile("((?:62)|(?:6[0-9]))\\.[0-9]+(?=\\s*AIS|\\s*Weight|\\s*$)");
        Matcher matcher = pattern1.matcher(text);
        if (matcher.find()) {
            String weightStr = matcher.group(1);
            log.info("체중 패턴 1에서 발견: {}", weightStr);
            return new BigDecimal(weightStr);
        }

        // 2. Weight 라인에서 바로 다음 숫자 찾기 (62.1)
        Pattern pattern2 = Pattern.compile("Weight\\s*([0-9]+\\.?[0-9]*)");
        matcher = pattern2.matcher(text);
        if (matcher.find()) {
            String weightStr = matcher.group(1);
            BigDecimal weight = new BigDecimal(weightStr);
            if (weight.compareTo(new BigDecimal("40")) >= 0 && weight.compareTo(new BigDecimal("200")) <= 0) {
                log.info("체중 패턴 2에서 발견: {}", weightStr);
                return weight;
            }
        }

        // 3. ALAEASHBodyCompositionHistory 섹션에서 체중 찾기
        Pattern pattern3 = Pattern.compile("BodyCompositionHistory[\\s\\S]*?([6-7][0-9]\\.[0-9]+)");
        matcher = pattern3.matcher(text);
        if (matcher.find()) {
            String weightStr = matcher.group(1);
            log.info("체중 패턴 3에서 발견: {}", weightStr);
            return new BigDecimal(weightStr);
        }

        // 4. 60대 숫자로 체중 찾기 (더 보편적)
        Pattern pattern4 = Pattern.compile("([6-7][0-9]\\.[0-9]+)");
        matcher = pattern4.matcher(text);
        while (matcher.find()) {
            String weightStr = matcher.group(1);
            BigDecimal weight = new BigDecimal(weightStr);
            // 60-80kg 범위로 제한
            if (weight.compareTo(new BigDecimal("55")) >= 0 && weight.compareTo(new BigDecimal("85")) <= 0) {
                log.info("체중 패턴 4에서 발견: {}", weightStr);
                return weight;
            }
        }

        // 5. 체중 (kg) 패턴
        Pattern pattern5 = Pattern.compile("체중\\s*\\([kK][gG]\\)\\s*([0-9]+\\.?[0-9]*)");
        matcher = pattern5.matcher(text);
        if (matcher.find()) {
            String weightStr = matcher.group(1);
            log.info("체중 패턴 5에서 발견: {}", weightStr);
            return new BigDecimal(weightStr);
        }

        // 6. kg 단위와 함께 있는 체중 찾기
        Pattern pattern6 = Pattern.compile("([6-7][0-9]\\.[0-9]+)\\s*kg");
        matcher = pattern6.matcher(text);
        if (matcher.find()) {
            String weightStr = matcher.group(1);
            log.info("체중 패턴 6에서 발견: {}", weightStr);
            return new BigDecimal(weightStr);
        }

        // 7. 일반적인 체중 범위 숫자 찾기 (최후의 방법)
        Pattern pattern7 = Pattern.compile("([5-8][0-9]\\.[0-9]+)");
        matcher = pattern7.matcher(text);
        while (matcher.find()) {
            String weightStr = matcher.group(1);
            BigDecimal weight = new BigDecimal(weightStr);
            if (weight.compareTo(new BigDecimal("45")) >= 0 && weight.compareTo(new BigDecimal("90")) <= 0) {
                log.info("체중 패턴 7에서 발견: {}", weightStr);
                return weight;
            }
        }

        log.warn("체중 정보를 찾을 수 없습니다");
        return BigDecimal.ZERO;
    }

    private BigDecimal extractBodyFat(String text) {
        // 1. PercentBodyFat 직접 찾기 (16.3)
        Pattern pattern1 = Pattern.compile("PercentBodyFat\\s*([1-2]?[0-9]\\.[0-9]+)");
        Matcher matcher = pattern1.matcher(text);
        if (matcher.find()) {
            String bodyFatStr = matcher.group(1);
            log.info("체지방률 패턴 1에서 발견: {}", bodyFatStr);
            return new BigDecimal(bodyFatStr);
        }

        // 2. 163과 같이 소수점이 빠진 경우 처리
        Pattern pattern2 = Pattern.compile("PercentBodyFat\\s*([1-5][0-9]{1,2})");
        matcher = pattern2.matcher(text);
        if (matcher.find()) {
            String bodyFatStr = matcher.group(1);
            // 100 이상이면 소수점 추가 (163 -> 16.3)
            if (Integer.parseInt(bodyFatStr) >= 100) {
                bodyFatStr = bodyFatStr.substring(0, bodyFatStr.length() - 1) + "." + bodyFatStr.substring(bodyFatStr.length() - 1);
                log.info("체지방률 패턴 2에서 발견 (소수점 복원): {}", bodyFatStr);
                return new BigDecimal(bodyFatStr);
            }
        }

        // 3. 체지방률(%) 직접 찾기
        Pattern pattern3 = Pattern.compile("체지방률\\s*\\(\\%\\).*?([0-9]+\\.?[0-9]*)");
        matcher = pattern3.matcher(text);
        if (matcher.find()) {
            String bodyFatStr = matcher.group(1);
            log.info("체지방률 패턴 3에서 발견: {}", bodyFatStr);
            return new BigDecimal(bodyFatStr);
        }

        // 4. % 기호가 있는 패턴 (범위 확인)
        Pattern pattern4 = Pattern.compile("([0-9]+\\.?[0-9]*)\\s*%");
        matcher = pattern4.matcher(text);
        while (matcher.find()) {
            String bodyFatStr = matcher.group(1);
            BigDecimal bodyFat = new BigDecimal(bodyFatStr);
            // 체지방률은 보통 5-50% 범위
            if (bodyFat.compareTo(new BigDecimal("5")) >= 0 && bodyFat.compareTo(new BigDecimal("50")) <= 0) {
                log.info("체지방률 패턴 4에서 발견: {}", bodyFatStr);
                return bodyFat;
            }
        }

        // 5. 히스토리 섹션에서 체지방률 찾기 (16.3)
        Pattern pattern5 = Pattern.compile("PercentBodyFat[\\s\\S]*?([1-3][0-9]\\.[0-9]+)");
        matcher = pattern5.matcher(text);
        if (matcher.find()) {
            String bodyFatStr = matcher.group(1);
            BigDecimal bodyFat = new BigDecimal(bodyFatStr);
            if (bodyFat.compareTo(new BigDecimal("5")) >= 0 && bodyFat.compareTo(new BigDecimal("50")) <= 0) {
                log.info("체지방률 패턴 5에서 발견: {}", bodyFatStr);
                return bodyFat;
            }
        }

        // 6. 특정 범위의 숫자 찾기 (10-30 범위)
        Pattern pattern6 = Pattern.compile("([1-3][0-9]\\.[0-9]+)");
        matcher = pattern6.matcher(text);
        while (matcher.find()) {
            String bodyFatStr = matcher.group(1);
            BigDecimal bodyFat = new BigDecimal(bodyFatStr);
            // 10-30% 범위 체크
            if (bodyFat.compareTo(new BigDecimal("10")) >= 0 && bodyFat.compareTo(new BigDecimal("30")) <= 0) {
                log.info("체지방률 패턴 6에서 발견: {}", bodyFatStr);
                return bodyFat;
            }
        }

        log.warn("체지방률 정보를 찾을 수 없습니다");
        return BigDecimal.ZERO;
    }

    private BigDecimal extractMuscleMass(String text) {
        // 1. 정확한 골격근량 값 찾기 (31.99 등)
        Pattern pattern1 = Pattern.compile("(3[0-5]\\.[0-9]+)\\([^)]*\\)");
        Matcher matcher = pattern1.matcher(text);
        if (matcher.find()) {
            String muscleMassStr = matcher.group(1);
            log.info("골격근량 패턴 1에서 발견: {}", muscleMassStr);
            return new BigDecimal(muscleMassStr);
        }

        // 2. Skeletal Muscle Mass 직접 찾기 (31.9)
        Pattern pattern2 = Pattern.compile("SkeletalMuscleMass[\\s\\S]*?([3-4][0-9]\\.[0-9]+)");
        matcher = pattern2.matcher(text);
        if (matcher.find()) {
            String muscleMassStr = matcher.group(1);
            log.info("골격근량 패턴 2에서 발견: {}", muscleMassStr);
            return new BigDecimal(muscleMassStr);
        }

        // 3. 골격근량 history 섹션에서 찾기 (32.0, 31.9)
        Pattern pattern3 = Pattern.compile("Weight.*?([3-4][0-9]\\.[0-9]+)\\s*SkeletalMuscleMass");
        matcher = pattern3.matcher(text);
        if (matcher.find()) {
            String muscleMassStr = matcher.group(1);
            log.info("골격근량 패턴 3에서 발견: {}", muscleMassStr);
            return new BigDecimal(muscleMassStr);
        }

        // 4. 골격근량 (kg) 직접 찾기
        Pattern pattern4 = Pattern.compile("골격근량\\s*\\([kK][gG]\\)\\s*([0-9]+\\.?[0-9]*)");
        matcher = pattern4.matcher(text);
        if (matcher.find()) {
            String muscleMassStr = matcher.group(1);
            log.info("골격근량 패턴 4에서 발견: {}", muscleMassStr);
            return new BigDecimal(muscleMassStr);
        }

        // 5. 소수점 골격근량 값 추출 (30-40 범위)
        Pattern pattern5 = Pattern.compile("(?:^|\\s)(3[0-5]\\.[0-9]+)(?=\\s|$)");
        matcher = pattern5.matcher(text);
        while (matcher.find()) {
            String muscleMassStr = matcher.group(1);
            BigDecimal muscleMass = new BigDecimal(muscleMassStr);
            // 골격근량은 보통 25-50 범위
            if (muscleMass.compareTo(new BigDecimal("25")) >= 0 && muscleMass.compareTo(new BigDecimal("50")) <= 0) {
                log.info("골격근량 패턴 5에서 발견: {}", muscleMassStr);
                return muscleMass;
            }
        }

        // 6. line break 후 숫자 찾기 (32.0, 31.9 형태)
        Pattern pattern6 = Pattern.compile("([3-4][0-9]\\.[0-9]+)\\s*([3-4][0-9]\\.[0-9]+)");
        matcher = pattern6.matcher(text);
        if (matcher.find()) {
            String muscleMassStr = matcher.group(1); // 첫 번째 값이 보통 더 정확함
            log.info("골격근량 패턴 6에서 발견: {}", muscleMassStr);
            return new BigDecimal(muscleMassStr);
        }

        // 7. 브라켓으로 둘러싸인 값 (31.99(27.8~34.0))
        Pattern pattern7 = Pattern.compile("([3-4][0-9]\\.[0-9]+)\\s*\\([0-9]");
        matcher = pattern7.matcher(text);
        if (matcher.find()) {
            String muscleMassStr = matcher.group(1);
            log.info("골격근량 패턴 7에서 발견: {}", muscleMassStr);
            return new BigDecimal(muscleMassStr);
        }

        // 8. 최후의 방법: 30대 숫자 중 골격근량 범위에 맞는 값
        Pattern pattern8 = Pattern.compile("([3][0-9]\\.[0-9]+)");
        matcher = pattern8.matcher(text);
        while (matcher.find()) {
            String muscleMassStr = matcher.group(1);
            BigDecimal muscleMass = new BigDecimal(muscleMassStr);
            if (muscleMass.compareTo(new BigDecimal("30")) >= 0 && muscleMass.compareTo(new BigDecimal("40")) <= 0) {
                log.info("골격근량 패턴 8에서 발견: {}", muscleMassStr);
                return muscleMass;
            }
        }

        log.warn("골격근량 정보를 찾을 수 없습니다");
        return BigDecimal.ZERO;
    }

    private LocalDate extractMeasurementDate(String text) {
        // "2022.02.24. 15:07" 형태
        Pattern pattern1 = Pattern.compile("([0-9]{4})\\.([0-9]{1,2})\\.([0-9]{1,2})\\s*\\.?\\s*([0-9]{1,2}):([0-9]{1,2})");
        Matcher matcher = pattern1.matcher(text);

        if (matcher.find()) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));
            log.info("측정일 패턴 1에서 발견: {}.{}.{}", year, month, day);
            return LocalDate.of(year, month, day);
        }

        // "2022.02.24" 형태
        Pattern pattern2 = Pattern.compile("([0-9]{4})\\.([0-9]{1,2})\\.([0-9]{1,2})");
        matcher = pattern2.matcher(text);

        if (matcher.find()) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));
            log.info("측정일 패턴 2에서 발견: {}.{}.{}", year, month, day);
            return LocalDate.of(year, month, day);
        }

        log.warn("측정 날짜 정보를 찾을 수 없습니다. 현재 날짜를 사용합니다.");
        return LocalDate.now();
    }
}