package com.dkay229.webcam.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(value = "/stream-video", produces = "multipart/x-mixed-replace;boundary=frame")
    public void streamVideo(HttpServletResponse response) throws IOException {
        response.setContentType("multipart/x-mixed-replace;boundary=frame");

        logger.info("Streaming video feed");
        executor.submit(() -> {
            try {
                try (OutputStream out = response.getOutputStream()) {
                    Mat frame = new Mat();

                    while (true) {
                        if (camera.read(frame)) {
                            logger.info("Converting frame to JPEG");
                            byte[] frameBytes = convertMatToJpeg(frame);
                            logger.info("Write the frame as part of the MJPEG stream");
                            out.write(("--frame\r\n").getBytes());
                            out.write(("Content-Type: image/jpeg\r\n\r\n").getBytes());
                            out.write(frameBytes);
                            out.write("\r\n".getBytes());
                            out.flush();
                            logger.info("Sleeping for 100ms for approximately 10 FPS");
                            Thread.sleep(100); // Approximately 10 FPS
                        } else {
                            logger.warn("Error: Could not read frame from camera.");
                        }

                        if (response.isCommitted()) {
                            logger.info("Client disconnected. Stopping video stream.");
                            break;
                        }
                    }
                } catch (IOException e) {
                    logger.error("Client disconnected or error streaming video: ",e);
                } catch (InterruptedException e) {
                    logger.error("Stream interrupted",e);
                }
            } catch (Exception e) {
                logger.error("Error streaming video", e);
            }
        });
    }
    // Utility method to convert OpenCV Mat object to JPEG byte array
    private byte[] convertMatToJpeg(Mat frame) throws IOException {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, mob);
        return mob.toArray();
    }
}
