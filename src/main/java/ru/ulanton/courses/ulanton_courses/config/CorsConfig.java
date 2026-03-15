package ru.ulanton.courses.ulanton_courses.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Все эндпоинты
                        .allowedOrigins("http://localhost:3000")  // Разрешенные источники
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Разрешенные методы
                        .allowedHeaders("*")  // Разрешенные заголовки
                        .allowCredentials(true)  // Разрешить куки/авторизацию
                        .maxAge(3600);  // Время кэширования preflight запросов (в секундах)
            }
        };
    }
}