package com.dkay229.webcam.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    // Replace with your own credentials or fetch them from environment variables
    private final String accessKeyId = "AKIAUQ4L3GPOZY5JLZV7";
    private final String secretAccessKey = "kcR6eL/rN41L3rxkze0q5eR36mC2j2z6tYFZGKDY";
    private final Region region = Region.US_EAST_2;  // Replace with your region

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        return S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}

