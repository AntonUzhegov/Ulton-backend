package ru.ulanton.courses.ulanton_courses.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ulanton.courses.ulanton_courses.dto.ContactFormDto;
import ru.ulanton.courses.ulanton_courses.services.EmailService;
import ru.ulanton.courses.ulanton_courses.repositories.CourseRepository;

@Controller
public class MainController {

    private final CourseRepository courseRepository;
    private final EmailService emailService;

    public MainController(CourseRepository courseRepository, EmailService emailService) {
        this.courseRepository = courseRepository;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAuthenticated", true);

            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        return "hello";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("contactForm", new ContactFormDto());
        return "contacts";
    }

    @PostMapping("/contacts/send")
    public String sendContact(@ModelAttribute("contactForm") ContactFormDto contactForm,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        // Валидация на стороне сервера
        if (contactForm.getName() == null || contactForm.getName().trim().isEmpty()) {
            model.addAttribute("error", "Пожалуйста, укажите ваше имя");
            model.addAttribute("contactForm", contactForm);
            return "contacts";
        }

        if (contactForm.getEmail() == null || contactForm.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "Пожалуйста, укажите ваш email");
            model.addAttribute("contactForm", contactForm);
            return "contacts";
        }

        if (contactForm.getMessage() == null || contactForm.getMessage().trim().isEmpty()) {
            model.addAttribute("error", "Пожалуйста, напишите сообщение");
            model.addAttribute("contactForm", contactForm);
            return "contacts";
        }

        if (contactForm.getTopic() == null || contactForm.getTopic().trim().isEmpty() ||
                contactForm.getTopic().equals("Выберите тему")) {
            model.addAttribute("error", "Пожалуйста, выберите тему обращения");
            model.addAttribute("contactForm", contactForm);
            return "contacts";
        }

        try {
            boolean success = emailService.sendContactMessage(
                    contactForm.getName().trim(),
                    contactForm.getEmail().trim(),
                    contactForm.getTopic(),
                    contactForm.getMessage().trim()
            );

            if (success) {
                redirectAttributes.addFlashAttribute("success",
                        "✅ Сообщение успешно отправлено! Мы свяжемся с вами в ближайшее время.");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "❌ Ошибка при отправке сообщения. Пожалуйста, попробуйте позже или напишите напрямую на uzhegov2006@gmail.com");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "❌ Ошибка: " + e.getMessage() + ". Пожалуйста, напишите напрямую на uzhegov2006@gmail.com");
        }

        return "redirect:/contacts";
    }

    @GetMapping("/errors/404")
    public String error404(Model model) {
        model.addAttribute("errorMessage", "Ара, ну нима пока такой страницы, выходи");
        return "error";
    }

    @GetMapping("/errors/500")
    public String error500(Model model) {
        model.addAttribute("errorMessage", "Внутренняя ошибка сервера");
        return "error";
    }

    @GetMapping("/errors/{code}")
    public String anyError(@PathVariable String code, Model model) {
        if ("404".equals(code)) {
            model.addAttribute("errorMessage", "Ара, ну нима пока такой страницы, выходи");
        } else {
            model.addAttribute("errorMessage", "Ошибка " + code);
        }
        return "error";
    }

    @GetMapping("/test-email")
    @ResponseBody
    public String testEmail() {
        try {
            boolean success = emailService.sendContactMessage(
                    "Тестовое Имя",
                    "test@example.com",
                    "Тестовая тема",
                    "Это тестовое сообщение для проверки работы email"
            );

            if (success) {
                return "✅ Email отправлен успешно! Проверьте почту uzhegov2006@gmail.com";
            } else {
                return "❌ Ошибка отправки email";
            }
        } catch (Exception e) {
            return "❌ Исключение: " + e.getMessage();
        }
    }
}