package com.dkay229.webcam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/camera")
    public String cameraPage() {
        return "camera"; // This will serve the 'camera.html' page
    }
}

