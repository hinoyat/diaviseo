package com.s206.health.nutrition.meal.service;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MealImageService {

    @Autowired
    private MinioClient minioClient;

    @Value("${spring.minio.bucket.name}")
    private String bucketName;

    // 버킷이 존재하는지 확인하고 없으면 생성
    public void checkBucket() {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("버킷 생성 중 오류가 발생했습니다 :" + e.getMessage(), e);
        }
    }

    // 식단 이미지를 업로드
    public String uploadMealImage(MultipartFile file) {
        checkBucket();

        try {
            // 유니크한 파일명 생성 (충돌 방지)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String objectName = "meal/" + UUID.randomUUID() + extension;

            // MinIO에 파일 업로드
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }


    // 이미지 URL 생성
    public String getMealImageUrl(String objectName) {
        if (objectName == null || objectName.isEmpty()) {
            return null;
        }

        // meal/ 접두사 자동 추가
        if (!objectName.startsWith("meal/")) {
            objectName = "meal/" + objectName;
        }

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(Method.GET)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("이미지 URL 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }


    // 이미지 파일을 삭제
    public void deleteMealImage(String objectName) {
        if (objectName == null || objectName.isEmpty()) {
            return; // 빈 URL이면 무시
        }

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("이미지 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
