package ru.ulanton.courses.ulanton_courses.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ulanton.courses.ulanton_courses.models.Lesson;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Optional<Lesson> findByCourse_SlugAndPosition(String slug, Integer position);

    @Query("SELECT lesson FROM Lesson lesson JOIN FETCH lesson.course WHERE lesson.course.slug = :slug AND lesson.position = :position")
    Optional<Lesson> findByCourseSlugAndPositionWithCourse(@Param("slug") String slug,
                                                           @Param("position") Integer position);

    List<Lesson> findByCourse_SlugOrderByPositionAsc(String slug);

    @Query("SELECT lesson FROM Lesson lesson JOIN FETCH lesson.course WHERE lesson.course.slug = :slug ORDER BY lesson.position ASC")
    List<Lesson> findAllByCourseSlugWithCourse(@Param("slug") String slug);
}