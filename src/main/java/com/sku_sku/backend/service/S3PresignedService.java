package com.sku_sku.backend.service;

import com.sku_sku.backend.dto.Request.S3DTO;
import com.sku_sku.backend.enums.AllowedFileType;
import com.sku_sku.backend.exception.NotAllowedFileTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Map;


@Service
public class S3PresignedService {
    private final S3Presigner s3Presigner;
    private final String bucketName;

    public S3PresignedService(
            @Value("${cloud.aws.credentials.access-key}") String accessKey,
            @Value("${cloud.aws.credentials.secret-key}") String secretKey,
            @Value("${cloud.aws.bucket}") String bucketName
    ) {
        this.bucketName = bucketName;
        this.s3Presigner = S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public Map<String, String> issuePresignedAndCdnUrl(S3DTO.PresignedUrlRequest req) {
        if (!AllowedFileType.isAllowedMimeType(req.getMimeType())) {
            throw new IllegalArgumentException("허용되지 않은 MIME 타입입니다.");
        }

        String key = "uploads/" + req.getFileName();
        URL presignedUrl = generatePresignedPutUrl(key, req.getMimeType());
        String cdnUrl = "https://d1gawugnyki65m.cloudfront.net/" + key;

        return Map.of(
                "uploadUrl", presignedUrl.toString(),
                "cdnUrl", cdnUrl
        );
    }

    private URL generatePresignedPutUrl(String key, String mimeType) {
        if (!AllowedFileType.isAllowedMimeType(mimeType)) {
            throw new NotAllowedFileTypeException();
        }

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(mimeType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url();
    }
}

