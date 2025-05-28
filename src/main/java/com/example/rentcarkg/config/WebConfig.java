package com.example.rentcarkg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
                // Путь для отдачи изображений
                String uploadsDir = Paths.get("uploads").toAbsolutePath().toString();

                registry.addResourceHandler("/uploads/images/**", "/static/images/**")
                        .addResourceLocations("file:" + uploadsDir + "/images/");
            }
        };
    }
}
