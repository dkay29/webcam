package com.dkay229.webcam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"com.dkay229.webcam"})
public class WebcamApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebcamApplication.class, args);
	}

}
