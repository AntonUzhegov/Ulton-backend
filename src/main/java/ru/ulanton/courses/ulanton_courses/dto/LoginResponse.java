package ru.ulanton.courses.ulanton_courses.dto;

public class LoginResponse {
    private String message;
    private boolean success;
    private UserDto user;
    private String token;  // Новое поле

    public LoginResponse() {}

    public LoginResponse(String message, boolean success, UserDto user) {
        this.message = message;
        this.success = success;
        this.user = user;
    }

    // Геттеры и сеттеры
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}