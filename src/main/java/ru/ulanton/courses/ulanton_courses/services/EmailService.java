package ru.ulanton.courses.ulanton_courses.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.ulanton.courses.ulanton_courses.util.EmailTemplates;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.admin.email}")
    private String adminEmail;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public boolean sendContactMessage(String name, String email, String topic, String message){
        try{
            SimpleMailMessage adminMessage = new SimpleMailMessage();
            adminMessage.setFrom(fromEmail);
            adminMessage.setTo(adminEmail);
            adminMessage.setSubject("Новое обращение с сайта Ulton: " + topic);
            adminMessage.setText(EmailTemplates.buildAdminMessage(name, email, topic, message));

            SimpleMailMessage userMessage = new SimpleMailMessage();
            userMessage.setFrom(fromEmail);
            userMessage.setTo(email);
            userMessage.setSubject("Ulton: Ваше сообщение получено");
            userMessage.setText(EmailTemplates.buildUserMessage(name, topic));

            mailSender.send(adminMessage);
            mailSender.send(userMessage);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}