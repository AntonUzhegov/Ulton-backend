package ru.ulanton.courses.ulanton_courses.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ulanton.courses.ulanton_courses.dto.response.LessonDTO;
import ru.ulanton.courses.ulanton_courses.exceptions.ResourceNotFoundException;
import ru.ulanton.courses.ulanton_courses.models.Course;
import ru.ulanton.courses.ulanton_courses.repositories.CourseRepository;
import ru.ulanton.courses.ulanton_courses.repositories.LessonRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {

    private static final Logger logger = LoggerFactory.getLogger(LessonService.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    public List<LessonDTO> getLessonsByCourseSlug(String slug){
        logger.debug("Fetching lessons for course slug: {}", slug);

        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "slug", slug));

        return course.getLessons().stream()
                .map(LessonDTO::new)
                .collect(Collectors.toList());
    }

    public LessonDTO getLessonByCourseSlugAndPosition(String slug, Integer position){
        logger.debug("Fetching lesson {} for course {}", position, slug);

        return lessonRepository.findByCourse_SlugAndPosition(slug, position)
                .map(LessonDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "position", position));
    }
}