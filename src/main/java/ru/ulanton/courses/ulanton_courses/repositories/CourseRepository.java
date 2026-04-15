package ru.ulanton.courses.ulanton_courses.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ulanton.courses.ulanton_courses.models.Course;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findBySlug(String slug);

    @Query("SELECT course FROM Course course LEFT JOIN FETCH course.lessons WHERE course.slug = :slug")
    Optional<Course> findBySlugWithLessons(@Param("slug") String slug);

    @Query("SELECT course FROM Course course LEFT JOIN FETCH course.lessons WHERE course.id = :id")
    Optional<Course> findByIdWithLessons(@Param("id") Long id);
}