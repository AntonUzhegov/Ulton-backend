package ru.ulanton.courses.ulanton_courses.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.ulanton.courses.ulanton_courses.dto.UserDto;
import ru.ulanton.courses.ulanton_courses.models.User;

import java.util.concurrent.TimeUnit;

@Service
public class RedisSessionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String USER_SESSION_KEY = "user:session:";
    private static final String TOKEN_KEY = "token:";
    private static final long SESSION_TIMEOUT = 30; // минут

    // Сохранить пользовательскую сессию
    public void saveUserSession(String token, UserDto user) {
        String key = USER_SESSION_KEY + token;
        redisTemplate.opsForValue().set(key, user, SESSION_TIMEOUT, TimeUnit.MINUTES);

        // Также сохраняем токен для быстрого поиска по userId
        String userTokenKey = TOKEN_KEY + user.getId();
        redisTemplate.opsForValue().set(userTokenKey, token, SESSION_TIMEOUT, TimeUnit.MINUTES);
    }

    // Получить пользователя по токену
    public UserDto getUserByToken(String token) {
        String key = USER_SESSION_KEY + token;
        return (UserDto) redisTemplate.opsForValue().get(key);
    }

    // Получить токен по userId
    public String getTokenByUserId(Long userId) {
        String key = TOKEN_KEY + userId;
        return (String) redisTemplate.opsForValue().get(key);
    }

    // Удалить сессию
    public void removeUserSession(String token) {
        String key = USER_SESSION_KEY + token;
        UserDto user = getUserByToken(token);

        if (user != null) {
            String userTokenKey = TOKEN_KEY + user.getId();
            redisTemplate.delete(userTokenKey);
        }

        redisTemplate.delete(key);
    }

    // Проверить, активна ли сессия
    public boolean isSessionActive(String token) {
        String key = USER_SESSION_KEY + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Обновить время жизни сессии
    public void refreshSession(String token) {
        String key = USER_SESSION_KEY + token;
        redisTemplate.expire(key, SESSION_TIMEOUT, TimeUnit.MINUTES);

        UserDto user = getUserByToken(token);
        if (user != null) {
            String userTokenKey = TOKEN_KEY + user.getId();
            redisTemplate.expire(userTokenKey, SESSION_TIMEOUT, TimeUnit.MINUTES);
        }
    }
}