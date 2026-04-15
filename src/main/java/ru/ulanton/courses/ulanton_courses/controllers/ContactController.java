package ru.ulanton.courses.ulanton_courses.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ulanton.courses.ulanton_courses.dto.request.ContactFormDTO;
import ru.ulanton.courses.ulanton_courses.dto.response.MessageResponse;
import ru.ulanton.courses.ulanton_courses.services.ContactService;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendContactMessage(@Valid @RequestBody
                                                                  ContactFormDTO contactFormDTO){
        MessageResponse response = contactService.sendContactMessage(contactFormDTO);

        if(response.isSuccess()){
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<MessageResponse> healthCheck(){
        return ResponseEntity.ok(new MessageResponse("Сервер отправки сообщений запущен",
                true));
    }
}