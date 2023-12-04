package com.mot.services.email;

import com.mot.appUser.AppUser;
import com.mot.dtos.EmailAddressDTO;
import com.mot.dtos.NotificationDTO;
import org.springframework.http.ResponseEntity;


public interface EmailService {
    ResponseEntity<String> confirmEmail(String token);
    ResponseEntity<String> resendVerificationEmail(EmailAddressDTO emailAddressDTO);

    void sendEmail(NotificationDTO notification);

    void sendVerificationEmail(AppUser appUser);
}
