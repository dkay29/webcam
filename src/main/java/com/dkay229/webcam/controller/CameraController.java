package com.dkay229.webcam.controller;

import com.dkay229.webcam.service.S3Service;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;


@RestController
public class CameraController {
    private static final Logger logger = LoggerFactory.getLogger(CameraController.class);

    private static final DateTimeFormatter yyyyMMdd_HHmmss = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    @Autowired
    private S3Service s3Service;

    @GetMapping("/camera-feed")
    @ResponseBody
    public String getCameraFeed() throws IOException {
        // Open the camera (0 is the default camera)
        logger.info("opening camera");
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            return "Error: Could not open camera.";
        }

        Mat frame = new Mat();
        logger.info("capturing frame");
        camera.read(frame); // Capture a frame

        // Save the frame as an image (in memory)
        logger.info("saving frame as image");
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, matOfByte);

        logger.info("converting image to BufferedImage");
        byte[] byteArray = matOfByte.toArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        BufferedImage image = ImageIO.read(bis);

        logger.info("converting image to Base64 string");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        logger.info("uploading image to S3");
        String fileName = LocalDateTime.now().format(yyyyMMdd_HHmmss) + ".jpg";
        Path tempFile = Files.createTempFile(fileName, ".jpg");
        Files.write(tempFile, baos.toByteArray());

        s3Service.uploadFile(fileName, tempFile.toFile());
        List<String> s3FileList = s3Service.listFiles();
        Files.write(tempFile, baos.toByteArray());

        logger.info("converting image to encoded string");
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        logger.info("releasing camera");
        camera.release(); // Close the camera

        // Return the image as a Base64 string in HTML
        logger.info("returning image");
        return "<img src='data:image/jpg;base64," + base64Image + "' />" + s3FileList.toString();
    }
}
