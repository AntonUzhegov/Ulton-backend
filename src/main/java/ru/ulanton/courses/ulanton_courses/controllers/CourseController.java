package ru.ulanton.courses.ulanton_courses.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ulanton.courses.ulanton_courses.dto.CourseDto;
import ru.ulanton.courses.ulanton_courses.dto.LessonDto;
import ru.ulanton.courses.ulanton_courses.dto.MessageResponse;
import ru.ulanton.courses.ulanton_courses.models.Course;
import ru.ulanton.courses.ulanton_courses.models.Lesson;
import ru.ulanton.courses.ulanton_courses.repositories.CourseRepository;
import ru.ulanton.courses.ulanton_courses.repositories.LessonRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:3000")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    // Получить все курсы
    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        List<CourseDto> courseDtos = courses.stream()
                .map(CourseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseDtos);
    }

    // Получить курс по slug
    @GetMapping("/{slug}")
    public ResponseEntity<?> getCourseBySlug(@PathVariable String slug) {
        return courseRepository.findBySlug(slug)
                .map(course -> ResponseEntity.ok(new CourseDto(course)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Получить уроки курса
    @GetMapping("/{slug}/lessons")
    public ResponseEntity<?> getCourseLessons(@PathVariable String slug) {
        return courseRepository.findBySlug(slug)
                .map(course -> {
                    List<LessonDto> lessonDtos = course.getLessons().stream()
                            .map(LessonDto::new)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(lessonDtos);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Получить конкретный урок курса
    @GetMapping("/{slug}/lessons/{position}")
    public ResponseEntity<?> getLessonByPosition(
            @PathVariable String slug,
            @PathVariable Integer position) {

        return lessonRepository.findByCourse_SlugAndPosition(slug, position)
                .map(lesson -> ResponseEntity.ok(new LessonDto(lesson)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Поиск курсов
    @GetMapping("/search")
    public ResponseEntity<?> searchCourses(@RequestParam String query) {
        // Простой поиск по названию и описанию
        List<Course> courses = courseRepository.findAll().stream()
                .filter(course ->
                        course.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                course.getShortDescription().toLowerCase().contains(query.toLowerCase()) ||
                                course.getTags().toLowerCase().contains(query.toLowerCase())
                )
                .collect(Collectors.toList());

        List<CourseDto> courseDtos = courses.stream()
                .map(CourseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(courseDtos);
    }

    // Получить популярные теги
    @GetMapping("/tags/popular")
    public ResponseEntity<?> getPopularTags() {
        List<Course> courses = courseRepository.findAll();

        // Собираем все теги
        List<String> allTags = courses.stream()
                .map(Course::getTags)
                .filter(tags -> tags != null && !tags.isEmpty())
                .flatMap(tags -> List.of(tags.split(",")).stream())
                .map(String::trim)
                .collect(Collectors.toList());

        // Подсчитываем частоту тегов
        List<String> popularTags = allTags.stream()
                .distinct()
                .limit(10)
                .collect(Collectors.toList());

        return ResponseEntity.ok(popularTags);
    }

    // Получить курсы по тегу
    @GetMapping("/tag/{tag}")
    public ResponseEntity<?> getCoursesByTag(@PathVariable String tag) {
        List<Course> courses = courseRepository.findAll().stream()
                .filter(course ->
                        course.getTags() != null &&
                                course.getTags().toLowerCase().contains(tag.toLowerCase())
                )
                .collect(Collectors.toList());

        List<CourseDto> courseDtos = courses.stream()
                .map(CourseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(courseDtos);
    }
}