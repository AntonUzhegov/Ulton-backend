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

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
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

            return ResponseEntity.ok(new LoginResponse("Успешный вход", true, userDto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Неверный логин или пароль", false));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
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

            return ResponseEntity.ok(new MessageResponse("Регистрация успешна", true));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Ошибка регистрации: " + e.getMessage(), false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Выход выполнен успешно", true));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(new MessageResponse("Не авторизован", false));
        }

        User user = (User) authentication.getPrincipal();
        UserDto userDto = new UserDto(user);

        return ResponseEntity.ok(userDto);
    }
}