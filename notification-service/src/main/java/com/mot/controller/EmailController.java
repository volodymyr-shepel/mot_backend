package com.mot.controller;


import com.mot.dtos.NotificationDTO;
import com.mot.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/email")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping(path = "sendEmail")
    public void sendEmail(@RequestBody NotificationDTO emailRequest){
        emailService.sendNotification(emailRequest);
    }
}
