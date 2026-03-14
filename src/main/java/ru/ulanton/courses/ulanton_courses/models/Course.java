package ru.ulanton.courses.ulanton_courses.models;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String shortDescription;

    private String badge;
    private String difficulty;
    private Integer durationWeeks;
    private Integer lessonsCount;

    @Column(length = 500)
    private String tags;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private List<Lesson> lessons = new ArrayList<>();

    // Геттеры
    public Long getId() { return id; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public String getShortDescription() { return shortDescription; }
    public String getBadge() { return badge; }
    public String getDifficulty() { return difficulty; }
    public Integer getDurationWeeks() { return durationWeeks; }
    public Integer getLessonsCount() { return lessonsCount; }
    public String getTags() { return tags; }
    public List<Lesson> getLessons() { return lessons; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setTitle(String title) { this.title = title; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public void setBadge(String badge) { this.badge = badge; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setDurationWeeks(Integer durationWeeks) { this.durationWeeks = durationWeeks; }
    public void setLessonsCount(Integer lessonsCount) { this.lessonsCount = lessonsCount; }
    public void setTags(String tags) { this.tags = tags; }
    public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }
}