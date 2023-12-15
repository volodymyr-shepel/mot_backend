package com.mot.controller;

import com.mot.dtos.EmailAddressDTO;
import com.mot.service.email.EmailService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth/email")
public class EmailController {
    @Resource(name = "motEmailService")
    private final EmailService emailServiceImpl;

    public EmailController(EmailService emailServiceImpl) {
        this.emailServiceImpl = emailServiceImpl;
    }
    @PostMapping(path = "/resend-verification-email")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody EmailAddressDTO emailAddressDTO){
        return emailServiceImpl.resendVerificationEmail(emailAddressDTO);
    }
}
