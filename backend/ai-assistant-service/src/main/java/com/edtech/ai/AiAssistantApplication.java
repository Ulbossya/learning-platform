package com.edtech.ai;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.edtech.ai", "com.edtech.common"})
public class AiAssistantApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiAssistantApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("AI Assistant Service").version("1.0"));
    }
}
