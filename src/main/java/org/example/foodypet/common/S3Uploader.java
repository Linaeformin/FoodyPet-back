package org.example.foodypet.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Profile({"prod", "local"})
@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadMealDiaryImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String key = "meal-diaries/" + UUID.randomUUID() + extension;

        String bucketName = bucket == null ? null : bucket.trim();

        System.out.println("===== S3 UPLOAD CHECK =====");
        System.out.println("bucket = [" + bucketName + "]");
        System.out.println("fileName = [" + file.getOriginalFilename() + "]");
        System.out.println("contentType = [" + file.getContentType() + "]");
        System.out.println("===========================");

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

            return "https://" + bucketName + ".s3.ap-southeast-2.amazonaws.com/" + key;

        } catch (IOException e) {
            throw new RuntimeException("식단 일기 이미지를 업로드할 수 없습니다.", e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf("."));
    }
}
