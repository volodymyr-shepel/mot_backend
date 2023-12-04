package com.mot.controllers;

import com.mot.dtos.EmailAddressDTO;
import com.mot.services.email.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth/email")
public class EmailController {


    private final EmailService emailServiceImpl;

    public EmailController(EmailService emailServiceImpl) {
        this.emailServiceImpl = emailServiceImpl;
    }

    // used to confirm an email
    @GetMapping(path = "/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        return emailServiceImpl.confirmEmail(token);
    }
    @PostMapping(path = "/resend-verification-email")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody EmailAddressDTO emailAddressDTO){
        return emailServiceImpl.resendVerificationEmail(emailAddressDTO);
    }
}
