package ru.ulanton.courses.ulanton_courses.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final String adminEmail = "uzhegov2006@gmail.com";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendContactMessage(String name, String email, String topic, String message) {
        try {
            // Сообщение администратору
            SimpleMailMessage adminMessage = new SimpleMailMessage();
            adminMessage.setFrom(fromEmail);
            adminMessage.setTo(adminEmail);
            adminMessage.setSubject("Новое обращение с сайта Ulton: " + topic);

            String adminText = String.format(
                    "📧 НОВОЕ ОБРАЩЕНИЕ С САЙТА ULTON\n\n" +
                            "Имя: %s\n" +
                            "Email: %s\n" +
                            "Тема: %s\n\n" +
                            "Сообщение:\n%s\n\n" +
                            "---\n" +
                            "Это сообщение отправлено с формы обратной связи",
                    name, email, topic, message
            );

            adminMessage.setText(adminText);

            // Подтверждение пользователю
            SimpleMailMessage userMessage = new SimpleMailMessage();
            userMessage.setFrom(fromEmail);
            userMessage.setTo(email);
            userMessage.setSubject("Ulton: Ваше сообщение получено");

            String userText = String.format(
                    "Уважаемый %s,\n\n" +
                            "Спасибо за обращение в Ulton!\n" +
                            "Мы получили ваше сообщение на тему \"%s\".\n" +
                            "Наша команда рассмотрит его и свяжется с вами в ближайшее время.\n\n" +
                            "С уважением,\n" +
                            "Команда Ulton",
                    name, topic
            );

            userMessage.setText(userText);

            // Отправляем оба письма
            mailSender.send(adminMessage);
            mailSender.send(userMessage);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}