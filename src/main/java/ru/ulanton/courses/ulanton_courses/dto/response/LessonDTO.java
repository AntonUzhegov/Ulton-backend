package ru.ulanton.courses.ulanton_courses.dto.response;

import ru.ulanton.courses.ulanton_courses.models.Lesson;

public class LessonDTO {
    private Long id;
    private Integer position;
    private String title;
    private String content;
    private Long courseId;

    public LessonDTO(){}

    public LessonDTO(Lesson lesson){
        this.id = lesson.getId();
        this.position = lesson.getPosition();
        this.title = lesson.getTitle();
        this.content = lesson.getContent();

        if(lesson.getCourse() != null){
            this.courseId = lesson.getCourse().getId();
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }
    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Long getCourseId() {
        return courseId;
    }
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}