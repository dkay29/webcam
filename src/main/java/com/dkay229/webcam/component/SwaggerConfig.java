package com.dkay229.webcam.component;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Camera API", version = "1.0", description = "Documentation for Camera API"))
public class SwaggerConfig {
    // Additional configurations can go here if necessary.
}
