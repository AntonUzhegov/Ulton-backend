package ru.ulanton.courses.ulanton_courses.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.ulanton.courses.ulanton_courses.dto.*;
import ru.ulanton.courses.ulanton_courses.models.User;
import ru.ulanton.courses.ulanton_courses.repositories.UserRepository;
import ru.ulanton.courses.ulanton_courses.services.RedisSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private RedisSessionService redisSessionService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.debug("Login attempt for user: {}", loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Получаем пользователя из БД
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Обновляем время последнего входа
            user.setLastLogin(java.time.LocalDateTime.now());
            userRepository.save(user);

            // Создаем DTO ответа
            UserDto userDto = new UserDto(user);

            // Генерируем токен сессии
            String sessionToken = UUID.randomUUID().toString();

            // Пробуем сохранить в Redis, но не падаем если Redis не доступен
            if (redisSessionService != null) {
                try {
                    redisSessionService.saveUserSession(sessionToken, userDto);
                    logger.debug("Session saved to Redis for user: {}", user.getUsername());
                } catch (Exception e) {
                    logger.warn("Failed to save session to Redis: {}", e.getMessage());
                }
            }

            // Добавляем токен в ответ
            LoginResponse response = new LoginResponse("Успешный вход", true, userDto);
            response.setToken(sessionToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Неверный логин или пароль", false));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        logger.debug("Registration attempt for user: {}", registerRequest.getUsername());

        // Проверка совпадения паролей
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Пароли не совпадают", false));
        }

        // Проверка существования email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Email уже используется", false));
        }

        // Проверка существования username
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Имя пользователя уже занято", false));
        }

        try {
            // Создание нового пользователя
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());

            Set<String> roles = new HashSet<>();
            roles.add("USER");
            user.setRoles(roles);

            user.setCreatedAt(java.time.LocalDateTime.now());
            user.setEnabled(true);

            userRepository.save(user);

            logger.info("User registered successfully: {}", user.getUsername());

            return ResponseEntity.ok(new MessageResponse("Регистрация успешна", true));

        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Ошибка регистрации: " + e.getMessage(), false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ") && redisSessionService != null) {
            try {
                String sessionToken = token.substring(7);
                redisSessionService.removeUserSession(sessionToken);
                logger.debug("Session removed from Redis");
            } catch (Exception e) {
                logger.warn("Failed to remove session from Redis: {}", e.getMessage());
            }
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Выход выполнен успешно", true));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String sessionToken = token.substring(7);

            // Сначала пробуем получить из Redis
            if (redisSessionService != null) {
                try {
                    UserDto userDto = redisSessionService.getUserByToken(sessionToken);
                    if (userDto != null) {
                        logger.debug("User found in Redis: {}", userDto.getUsername());
                        return ResponseEntity.ok(userDto);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to get user from Redis: {}", e.getMessage());
                }
            }

            // Если нет в Redis, пробуем получить из SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {

                Object principal = authentication.getPrincipal();
                if (principal instanceof User) {
                    User user = (User) principal;
                    logger.debug("User found in SecurityContext: {}", user.getUsername());
                    return ResponseEntity.ok(new UserDto(user));
                }
            }
        }

        return ResponseEntity.ok(new MessageResponse("Не авторизован", false));
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(new MessageResponse("Auth service is running", true));
    }
}