package com.s206.health.config;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.InitializingBean;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
public class MinioConfig {

    @Value("${spring.minio.endpoint}")
    private String endpoint;

    @Value("${spring.minio.accessKey}")
    private String accessKey;

    @Value("${spring.minio.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        // SSL 검증 비활성화를 위한 OkHttpClient 설정
        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .hostnameVerifier((hostname, session) -> true)
                .sslSocketFactory(createTrustAllSSLSocketFactory(), new TrustAllCerts())
                .build();

        // 설정된 정보로 MinIO 클라이언트 빌드 및 반환
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .httpClient(httpClient)
                .build();
    }

    // SSL 검증을 비활성화하는 SocketFactory 생성
    private SSLSocketFactory createTrustAllSSLSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] { new TrustAllCerts() };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("SSL 설정 중 오류 발생", e);
        }
    }

    // 모든 인증서를 신뢰하는 TrustManager
    private static class TrustAllCerts implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
    }

    // 글로벌 HTTPS URL 연결에 대한 SSL 검증 비활성화
    @Bean
    public InitializingBean disableSSLValidation() {
        return () -> {
            try {
                TrustManager[] trustAllCerts = new TrustManager[] { new TrustAllCerts() };
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            } catch (Exception e) {
                throw new RuntimeException("SSL 검증 비활성화 중 오류 발생", e);
            }
        };
    }
}