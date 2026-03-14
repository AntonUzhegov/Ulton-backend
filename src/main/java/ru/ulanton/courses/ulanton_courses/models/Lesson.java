package ru.ulanton.courses.ulanton_courses.models;

import jakarta.persistence.*;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer position;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Геттеры
    public Long getId() { return id; }
    public Integer getPosition() { return position; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Course getCourse() { return course; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setPosition(Integer position) { this.position = position; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setCourse(Course course) { this.course = course; }
}