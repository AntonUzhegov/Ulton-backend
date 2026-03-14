package ru.ulanton.courses.ulanton_courses.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ulanton.courses.ulanton_courses.dto.ContactFormDto;
import ru.ulanton.courses.ulanton_courses.dto.MessageResponse;
import ru.ulanton.courses.ulanton_courses.services.EmailService;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "http://localhost:3000")
public class ContactController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendContactMessage(@RequestBody ContactFormDto contactForm) {

        // Валидация
        if (contactForm.getName() == null || contactForm.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Пожалуйста, укажите ваше имя", false));
        }

        if (contactForm.getEmail() == null || contactForm.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Пожалуйста, укажите ваш email", false));
        }

        if (contactForm.getMessage() == null || contactForm.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Пожалуйста, напишите сообщение", false));
        }

        if (contactForm.getTopic() == null || contactForm.getTopic().trim().isEmpty() ||
                contactForm.getTopic().equals("Выберите тему")) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Пожалуйста, выберите тему обращения", false));
        }

        try {
            // Отправляем сообщение
            boolean success = emailService.sendContactMessage(
                    contactForm.getName().trim(),
                    contactForm.getEmail().trim(),
                    contactForm.getTopic(),
                    contactForm.getMessage().trim()
            );

            if (success) {
                return ResponseEntity.ok(new MessageResponse(
                        "✅ Сообщение успешно отправлено! Мы свяжемся с вами в ближайшее время.",
                        true
                ));
            } else {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse(
                                "❌ Ошибка при отправке сообщения. Пожалуйста, попробуйте позже.",
                                false
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(
                            "❌ Ошибка: " + e.getMessage(),
                            false
                    ));
        }
    }

    // Простой эндпоинт для проверки
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(new MessageResponse("Contact service is running", true));
    }
}