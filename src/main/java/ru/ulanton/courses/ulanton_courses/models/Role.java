package ru.ulanton.courses.ulanton_courses.models;

public enum Role {
    USER("Обычный пользователь"),
    ADMIN("Администратор"),
    MODERATOR("Модератор");

    private final String description;

    Role(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}