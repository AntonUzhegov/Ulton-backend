package ru.ulanton.courses.ulanton_courses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UlAntonCoursesApplication {

	public static void main(String[] args) {
		SpringApplication.run(UlAntonCoursesApplication.class, args);
	}

}
