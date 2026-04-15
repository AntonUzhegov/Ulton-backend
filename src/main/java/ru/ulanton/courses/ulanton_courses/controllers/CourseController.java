package ru.ulanton.courses.ulanton_courses.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ulanton.courses.ulanton_courses.dto.response.CourseDTO;
import ru.ulanton.courses.ulanton_courses.dto.response.LessonDTO;
import ru.ulanton.courses.ulanton_courses.dto.response.MessageResponse;
import ru.ulanton.courses.ulanton_courses.services.CourseService;
import ru.ulanton.courses.ulanton_courses.services.LessonService;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private LessonService lessonService;

    @GetMapping("")
    public ResponseEntity<List<CourseDTO>> getAllCourses(){
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CourseDTO> getCourseBySlug(@PathVariable String slug){
        return ResponseEntity.ok(courseService.getCourseBySlug(slug));
    }

    @GetMapping("/{slug}/lessons")
    public ResponseEntity<List<LessonDTO>> getCourseLessons(@PathVariable String slug){
        return ResponseEntity.ok(lessonService.getLessonsByCourseSlug(slug));
    }

    @GetMapping("/{slug}/lessons/{position}")
    public ResponseEntity<LessonDTO> getLessonByPosition(@PathVariable String slug,
                                                         @PathVariable Integer position){
        return ResponseEntity.ok(lessonService.getLessonByCourseSlugAndPosition(slug, position));
    }

    @GetMapping("/health")
    public ResponseEntity<MessageResponse> healthCheck(){
        return ResponseEntity.ok(new MessageResponse("Сервис обработки курсов работает",
                true));
    }
}