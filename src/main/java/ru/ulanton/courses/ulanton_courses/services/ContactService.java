package ru.ulanton.courses.ulanton_courses.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ulanton.courses.ulanton_courses.dto.request.ContactFormDTO;
import ru.ulanton.courses.ulanton_courses.dto.response.MessageResponse;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    @Autowired
    private EmailService emailService;

    public MessageResponse sendContactMessage(ContactFormDTO contactFormDTO) {
        try{
            boolean success = emailService.sendContactMessage(
                    contactFormDTO.getName().trim(),
                    contactFormDTO.getEmail().trim(),
                    contactFormDTO.getTopic().trim(),
                    contactFormDTO.getMessage().trim()
            );

            if(success){
                logger.info("Contact message sent from: {}", contactFormDTO.getEmail());
                return new MessageResponse(
                        "Сообщение успешно отправлено! Мы свяжемся с вами в ближайшее время.",
                        true
                );
            } else {
                return new MessageResponse("Ошибка при отправке сообщения. " +
                        "Пожалуйста попробуйте позже.",
                        false);
            }
        } catch (Exception e) {
            logger.error("Failed to send contact message: {}", e.getMessage());
            return new MessageResponse("Ошибка: " + e.getMessage(), false);
        }
    }
}