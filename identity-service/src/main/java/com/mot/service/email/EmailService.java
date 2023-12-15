package com.mot.service.email;

import com.mot.model.AppUser;
import com.mot.dtos.EmailAddressDTO;
import com.mot.dtos.NotificationDTO;
import org.springframework.http.ResponseEntity;


public interface EmailService {

    ResponseEntity<String> resendVerificationEmail(EmailAddressDTO emailAddressDTO);

    void sendEmail(NotificationDTO notification);

    void sendVerificationEmail(AppUser appUser);

    void sendForgetPasswordEmail(AppUser user);
}
