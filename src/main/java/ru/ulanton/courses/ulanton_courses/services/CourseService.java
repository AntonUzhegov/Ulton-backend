package ru.ulanton.courses.ulanton_courses.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.ulanton.courses.ulanton_courses.dto.response.CourseDTO;
import ru.ulanton.courses.ulanton_courses.exceptions.ResourceNotFoundException;
import ru.ulanton.courses.ulanton_courses.models.Course;
import ru.ulanton.courses.ulanton_courses.repositories.CourseRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    @Autowired
    private CourseRepository courseRepository;

    @Cacheable(value = "courses", key = "all")
    public List<CourseDTO> getAllCourses(){
        List<Course> courses = courseRepository.findAll();

        logger.debug("Found courses {}", courses.size());

        return courses.stream()
                .map(CourseDTO::new)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "course", key = "#slug")
    public CourseDTO getCourseBySlug(String slug){
        logger.debug("Fetching course by slug: {}", slug);

        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "slug", slug));

        return new CourseDTO(course);
    }
}