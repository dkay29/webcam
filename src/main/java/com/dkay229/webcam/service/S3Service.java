package com.dkay229.webcam.service;


import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName = "dd-kk-s3";  // Replace with your actual bucket name

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(String key, File file) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
        System.out.println("File uploaded successfully to S3: " + key);
    }

    public File downloadFile(String key, String downloadFilePath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.getObject(getObjectRequest, Paths.get(downloadFilePath));
        System.out.println("File downloaded successfully from S3: " + key);
        return new File(downloadFilePath);
    }

    public List<String> listFiles() {
        return s3Client.listObjectsV2Paginator(builder -> builder.bucket(bucketName))
                .stream()
                .flatMap(r -> r.contents().stream())
                .map(S3Object::key)
                .collect(Collectors.toList());
    }
}
