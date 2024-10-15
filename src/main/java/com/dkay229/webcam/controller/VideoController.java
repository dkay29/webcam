package com.dkay229.webcam.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class VideoController implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    private VideoCapture camera;
    private ExecutorService executor;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        logger.info("Initializing camera and executor service");
        camera = new VideoCapture(0); // Default camera
        executor = Executors.newSingleThreadExecutor();
        if (!camera.isOpened()) {
            throw new RuntimeException("Error: Unable to open camera.");
        }
    }

    @Override
    public void destroy() {
        logger.info("Releasing camera and shutting down executor service");
        if (camera != null) {
            camera.release();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }

    @GetMapping(value = "/video-stream", produces = MediaType.IMAGE_JPEG_VALUE)
    public void streamVideo(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        response.setHeader("Cache-Control", "no-store");

        Mat frame = new Mat();
        MatOfByte matOfByte = new MatOfByte();

        while (true) {
            logger.info("Capturing video frame");
            if (camera.read(frame)) {
                // Convert the frame to RGB
                logger.info("Converting frame to RGB");
                Mat frameRGB = new Mat();
                Imgproc.cvtColor(frame, frameRGB, Imgproc.COLOR_BGR2RGB);

                // Encode the frame as a JPEG image in MatOfByte
                logger.info("Encoding frame as JPEG image");
                Imgcodecs.imencode(".jpg", frameRGB, matOfByte);

                // Write the byte array to the response
                logger.info("Writing byte array to response");
                byte[] byteArray = matOfByte.toArray();
                response.getOutputStream().write(byteArray);
                response.getOutputStream().flush();
                logger.info("Frame sent to client");
                // Reset the stream for the next frame
                matOfByte.release();

                // Add a 10ms pause to control the frame rate
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                throw new IOException("Failed to capture video frame");
            }
        }
    }
}
