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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:3000")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    // Получить все курсы - без кэширования
    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        try {
            logger.debug("Fetching all courses from database");
            List<Course> courses = courseRepository.findAll();
            List<CourseDto> courseDtos = courses.stream()
                    .map(CourseDto::new)
                    .collect(Collectors.toList());
            logger.debug("Found {} courses", courses.size());
            return ResponseEntity.ok(courseDtos);
        } catch (Exception e) {
            logger.error("Error fetching courses: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Ошибка загрузки курсов: " + e.getMessage(), false));
        }
    }

    // Получить курс по slug
    @GetMapping("/{slug}")
    public ResponseEntity<?> getCourseBySlug(@PathVariable String slug) {
        try {
            logger.debug("Fetching course by slug: {}", slug);
            return courseRepository.findBySlug(slug)
                    .map(course -> {
                        logger.debug("Found course: {}", course.getTitle());
                        return ResponseEntity.ok(new CourseDto(course));
                    })
                    .orElseGet(() -> {
                        logger.debug("Course not found with slug: {}", slug);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error fetching course by slug {}: {}", slug, e.getMessage());
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Ошибка загрузки курса", false));
        }
    }

    // Получить уроки курса
    @GetMapping("/{slug}/lessons")
    public ResponseEntity<?> getCourseLessons(@PathVariable String slug) {
        try {
            logger.debug("Fetching lessons for course slug: {}", slug);
            return courseRepository.findBySlug(slug)
                    .map(course -> {
                        List<LessonDto> lessonDtos = course.getLessons().stream()
                                .map(LessonDto::new)
                                .collect(Collectors.toList());
                        return ResponseEntity.ok(lessonDtos);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching lessons for course {}: {}", slug, e.getMessage());
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Ошибка загрузки уроков", false));
        }
    }

    // Получить конкретный урок курса
    @GetMapping("/{slug}/lessons/{position}")
    public ResponseEntity<?> getLessonByPosition(
            @PathVariable String slug,
            @PathVariable Integer position) {
        try {
            logger.debug("Fetching lesson {} for course {}", position, slug);
            return lessonRepository.findByCourse_SlugAndPosition(slug, position)
                    .map(lesson -> ResponseEntity.ok(new LessonDto(lesson)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching lesson: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Ошибка загрузки урока", false));
        }
    }

    // Поиск курсов
    @GetMapping("/search")
    public ResponseEntity<?> searchCourses(@RequestParam String query) {
        try {
            logger.debug("Searching courses with query: {}", query);
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
        } catch (Exception e) {
            logger.error("Error searching courses: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Ошибка поиска курсов", false));
        }
    }

    // Получить популярные теги
    @GetMapping("/tags/popular")
    public ResponseEntity<?> getPopularTags() {
        try {
            List<Course> courses = courseRepository.findAll();

            List<String> allTags = courses.stream()
                    .map(Course::getTags)
                    .filter(tags -> tags != null && !tags.isEmpty())
                    .flatMap(tags -> List.of(tags.split(",")).stream())
                    .map(String::trim)
                    .collect(Collectors.toList());

            List<String> popularTags = allTags.stream()
                    .distinct()
                    .limit(10)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(popularTags);
        } catch (Exception e) {
            logger.error("Error fetching tags: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Ошибка загрузки тегов", false));
        }
    }

    // Получить курсы по тегу
    @GetMapping("/tag/{tag}")
    public ResponseEntity<?> getCoursesByTag(@PathVariable String tag) {
        try {
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
        } catch (Exception e) {
            logger.error("Error fetching courses by tag: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Ошибка загрузки курсов по тегу", false));
        }
    }
}