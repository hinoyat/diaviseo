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

        // OS별로 tessdata 경로 설정
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
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
        } else if (osName.contains("linux") || osName.contains("unix")) {
            log.info("Linux/Unix 환경에서 실행 중...");
            String[] linuxPaths = {
                    "/usr/share/tesseract-ocr/4.00/tessdata",
                    "/usr/share/tesseract-ocr/5.00/tessdata",
                    "/usr/share/tesseract-ocr/tessdata",
                    "/usr/local/share/tessdata",
                    "/opt/tesseract/tessdata"
            };

            for (String path : linuxPaths) {
                if (new java.io.File(path).exists()) {
                    tesseract.setDatapath(path);
                    log.info("tessdata 경로 설정 완료: {}", path);
                    break;
                }
            }
        }

        try {
            // 빠른 처리를 위한 설정
            tesseract.setLanguage("eng");  // 영어만 사용 (빠름)
            log.info("언어 설정 성공: eng (고속 모드)");

            // 빠른 OCR 설정
            tesseract.setTessVariable("tessedit_ocr_engine_mode", "0");  // Legacy 엔진 (빠름)
            tesseract.setTessVariable("tessedit_pageseg_mode", "6");      // 단일 텍스트 블록
            tesseract.setTessVariable("classify_bln_numeric_mode", "1");   // 숫자 우선 인식
            tesseract.setTessVariable("tessedit_create_hocr", "0");       // HOCR 생성 안함
            tesseract.setTessVariable("tessedit_create_pdf", "0");        // PDF 생성 안함

            log.info("고속 OCR 설정 완료");
        } catch (Exception e) {
            log.error("언어 설정 실패: {}", e.getMessage());
            log.info("언어 설정 실패 - 기본 설정을 사용합니다.");
        }

        tesseract.setTessVariable("tessedit_char_whitelist",
                "0123456789.abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ가-힣()~-");
    }

    public BodyInfoCreateRequest extractBodyInfoFromImage(MultipartFile imageFile)
            throws IOException, TesseractException {

        log.info("인바디 이미지 OCR 처리 시작 (고속 모드)");

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

        BigDecimal height = extractHeight(ocrText);
        log.info("추출된 키: {}", height);

        LocalDate measurementDate = extractMeasurementDate(ocrText);
        log.info("추출된 측정일: {}", measurementDate);

        return BodyInfoCreateRequest.builder()
                .weight(weight)
                .bodyFat(bodyFat)
                .muscleMass(muscleMass)
                .height(height)
                .measurementDate(measurementDate)
                .build();
    }

    private BigDecimal extractWeight(String text) {
        Matcher matcher;
        // 1. Weight 라인에서 바로 다음 숫자 찾기 (선택)
        Pattern pattern2 = Pattern.compile("Weight\\s*([0-9]?[0-9]?[0-9]+\\.?[0-9]*)");
        matcher = pattern2.matcher(text);
        if (matcher.find()) {
            String weightStr = matcher.group(1);
            BigDecimal weight = new BigDecimal(weightStr);
            if (weight.compareTo(new BigDecimal("40")) >= 0 && weight.compareTo(new BigDecimal("200")) <= 0) {
                log.info("체중 패턴 1에서 발견: {}", weightStr);
                return weight;
            }
        }

        // 2. ALAEASHBodyCompositionHistory 섹션에서 체중 찾기 (선택)
        Pattern pattern3 = Pattern.compile("BodyCompositionHistory[\\s\\S]*?([0-9][0-9]\\.[0-9]+)");
        matcher = pattern3.matcher(text);
        if (matcher.find()) {
            String weightStr = matcher.group(1);
            log.info("체중 패턴 2에서 발견: {}", weightStr);
            return new BigDecimal(weightStr);
        }

        log.warn("체중 정보를 찾을 수 없습니다");
        return BigDecimal.ZERO;
    }

    private BigDecimal extractBodyFat(String text) {
        Matcher matcher;

        // 1. PercentBodyFat 직접 찾기
        Pattern pattern1 = Pattern.compile("PercentBodyFat\\s*([0-9]?[0-9]\\.[0-9]+)");
        matcher = pattern1.matcher(text);
        if (matcher.find()) {
            String bodyFatStr = matcher.group(1);
            log.info("체지방률 패턴 1에서 발견: {}", bodyFatStr);
            return new BigDecimal(bodyFatStr);
        }

        // 2. 93처럼 잘못 인식된 경우를 9.3으로 변환하여 체크
        Pattern pattern2 = Pattern.compile("PercentBodyFat[\\s\\w]*?([0-9]{2})");
        matcher = pattern2.matcher(text);
        if (matcher.find()) {
            String bodyFatStr = matcher.group(1);
            // 93을 9.3으로, 87을 8.7로 변환
            if (bodyFatStr.length() == 2) {
                String corrected = bodyFatStr.charAt(0) + "." + bodyFatStr.charAt(1);
                BigDecimal bodyFat = new BigDecimal(corrected);
                if (bodyFat.compareTo(new BigDecimal("3")) >= 0 && bodyFat.compareTo(new BigDecimal("40")) <= 0) {
                    log.info("체지방률 패턴 2에서 발견 (보정됨): {} -> {}", bodyFatStr, corrected);
                    return bodyFat;
                }
            }
        }

        log.warn("체지방률 정보를 찾을 수 없습니다");
        return BigDecimal.ZERO;
    }

    private BigDecimal extractMuscleMass(String text) {
        Matcher matcher;
        // 1. 소수점 골격근량 값 추출 (30-40 범위) (선택)
        Pattern pattern5 = Pattern.compile("(?:^|\\s)(3[0-5]\\.[0-9]+)(?=\\s|$)");
        matcher = pattern5.matcher(text);
        while (matcher.find()) {
            String muscleMassStr = matcher.group(1);
            BigDecimal muscleMass = new BigDecimal(muscleMassStr);
            // 골격근량은 보통 25-50 범위
            if (muscleMass.compareTo(new BigDecimal("10")) >= 0 && muscleMass.compareTo(new BigDecimal("50")) <= 0) {
                log.info("골격근량 패턴 1에서 발견: {}", muscleMassStr);
                return muscleMass;
            }
        }

        // 2. 붙어있는 골격근량 숫자 분리 (32.031.9 형태)
        Pattern pattern6 = Pattern.compile("([0-9][0-9]\\.[0-9]+)([0-9][0-9]\\.[0-9]+)");
        matcher = pattern6.matcher(text);
        if (matcher.find()) {
            String firstValue = matcher.group(1);
            String secondValue = matcher.group(2);
            log.info("붙어있는 골격근량 값 발견: {} 와 {}", firstValue, secondValue);

            // 두 번째 값이 보통 더 최신
            BigDecimal mass2 = new BigDecimal(secondValue);
            if (mass2.compareTo(new BigDecimal("10")) >= 0 && mass2.compareTo(new BigDecimal("50")) <= 0) {
                log.info("골격근량 패턴 2에서 발견 (두 번째 값): {}", secondValue);
                return mass2;
            }

            // 첫 번째 값도 확인
            BigDecimal mass1 = new BigDecimal(firstValue);
            if (mass1.compareTo(new BigDecimal("10")) >= 0 && mass1.compareTo(new BigDecimal("50")) <= 0) {
                log.info("골격근량 패턴 2에서 발견 (첫 번째 값): {}", firstValue);
                return mass1;
            }
        }

        log.warn("골격근량 정보를 찾을 수 없습니다");
        return BigDecimal.ZERO;
    }

    private BigDecimal extractHeight(String text) {
        Matcher matcher;
        // 3. 첫 번째 줄에서 키 정보 찾기 (인바디 헤더 부분)
        Pattern pattern3 = Pattern.compile("([1-2][0-9][0-9])cm");
        matcher = pattern3.matcher(text);
        if (matcher.find()) {
            String heightStr = matcher.group(1);
            BigDecimal height = new BigDecimal(heightStr);
            if (height.compareTo(new BigDecimal("140")) >= 0 && height.compareTo(new BigDecimal("210")) <= 0) {
                log.info("키 패턴 3에서 발견: {}cm", heightStr);
                return height;
            }
        }

        log.warn("키 정보를 찾을 수 없습니다");
        return BigDecimal.ZERO;
    }

    private LocalDate extractMeasurementDate(String text) {
        Matcher matcher;
        // "2022.02.24" 형태
        Pattern pattern1 = Pattern.compile("([0-9]{4})\\.([0-9]{1,2})\\.([0-9]{1,2})");
        matcher = pattern1.matcher(text);

        if (matcher.find()) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));
            log.info("측정일 패턴 1에서 발견: {}.{}.{}", year, month, day);
            return LocalDate.of(year, month, day);
        }

        log.warn("측정 날짜 정보를 찾을 수 없습니다. 현재 날짜를 사용합니다.");
        return LocalDate.now();
    }
}