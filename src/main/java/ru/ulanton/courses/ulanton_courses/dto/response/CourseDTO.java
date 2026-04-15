package ru.ulanton.courses.ulanton_courses.dto.response;

import ru.ulanton.courses.ulanton_courses.models.Course;

import java.util.List;
import java.util.stream.Collectors;

public class CourseDTO {
    private Long id;
    private String slug;
    private String title;
    private String shortDescription;
    private String badge;
    private String difficulty;
    private Integer durationWeeks;
    private Integer lessonsCount;
    private String tags;
    private List<LessonDTO> lessons;

    public CourseDTO(){}

    public CourseDTO(Course course){
        this.id = course.getId();
        this.slug = course.getSlug();
        this.title = course.getTitle();
        this.shortDescription = course.getShortDescription();
        this.badge = course.getBadge();
        this.difficulty = course.getDifficulty();
        this.durationWeeks = course.getDurationWeeks();
        this.lessonsCount = course.getLessonsCount();
        this.tags = course.getTags();

        if(course.getLessons() != null){
            this.lessons = course.getLessons()
                    .stream().map(LessonDTO::new)
                    .collect(Collectors.toList());
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getBadge() {
        return badge;
    }
    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getDurationWeeks() {
        return durationWeeks;
    }
    public void setDurationWeeks(Integer durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public Integer getLessonsCount() {
        return lessonsCount;
    }
    public void setLessonsCount(Integer lessonsCount) {
        this.lessonsCount = lessonsCount;
    }

    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<LessonDTO> getLessons() {
        return lessons;
    }
    public void setLessons(List<LessonDTO> lessons) {
        this.lessons = lessons;
    }
}