package ru.ulanton.courses.ulanton_courses.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ulanton.courses.ulanton_courses.dto.request.LoginRequest;
import ru.ulanton.courses.ulanton_courses.dto.request.RegisterRequest;
import ru.ulanton.courses.ulanton_courses.dto.response.JwtResponse;
import ru.ulanton.courses.ulanton_courses.dto.response.MessageResponse;
import ru.ulanton.courses.ulanton_courses.dto.response.UserDTO;
import ru.ulanton.courses.ulanton_courses.exceptions.BadRequestException;
import ru.ulanton.courses.ulanton_courses.services.AuthService;
import ru.ulanton.courses.ulanton_courses.services.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest){
        JwtResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest registerRequest){
        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())){
            throw new BadRequestException("Пароли не совпадают");
        }

        userService.register(registerRequest);
        return ResponseEntity.ok(new MessageResponse("Регистрация прошла успешно",
                true));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(){
        authService.logout();
        return ResponseEntity.ok(new MessageResponse("Выход выполнен успешно", true));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(){
        UserDTO user = authService.getCurrentUser();
        if(user != null){
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Не авторизован", false));
    }

    @GetMapping("/health")
    public ResponseEntity<MessageResponse> healthCheck(){
        return ResponseEntity.ok(new MessageResponse("Сервис авторизации и регистрации запущен", true));
    }
}