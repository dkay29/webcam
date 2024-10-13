package com.dkay229.webcam.controller;

import com.dkay229.webcam.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    // Upload file to S3
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        s3Service.uploadFile(multipartFile.getOriginalFilename(), file);
        return "File uploaded successfully!";
    }

    // Upload an image (jpg) to S3
    @PostMapping("/upload-image")
    public String uploadImage(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();

        // Check if the file is a jpg image
        if (originalFileName != null && originalFileName.toLowerCase().endsWith(".jpg")) {
            // Create a temporary file to upload
            File file = new File(System.getProperty("java.io.tmpdir") + "/" + originalFileName);
            multipartFile.transferTo(file);

            // Upload the image to the S3 bucket
            s3Service.uploadFile(originalFileName, file);
            return "Image uploaded successfully!";
        } else {
            return "Only .jpg images are allowed!";
        }
    }

    // Download file from S3 bucket
    @GetMapping("/download/{key}")
    public String downloadFile(@PathVariable String key) {
        String downloadFilePath = System.getProperty("java.io.tmpdir") + "/" + key;
        s3Service.downloadFile(key, downloadFilePath);
        return "File downloaded to: " + downloadFilePath;
    }

    // List all files in the S3 bucket
    @GetMapping("/list")
    public List<String> listFiles() {
        return s3Service.listFiles();
    }
}
