package com.mot.service;

import com.mot.dtos.NotificationDTO;
import com.mot.util.NotificationType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service

public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${app.email-from}")
    private String emailSender;

    @Value("${app.email-from-name}")
    private String emailSenderName;

    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendNotification(NotificationDTO notification) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");


            helper.setText(generateEmailContent(notification.notificationFields(),notification.notificationType()), true);
            helper.setTo(notification.recipient());
            helper.setSubject(notification.subject());
            helper.setFrom(emailSender, emailSenderName);
            javaMailSender.send(mimeMessage);

            ResponseEntity.ok("The email has been sent successfully");
        } catch (MessagingException | UnsupportedEncodingException e) {
            ResponseEntity.badRequest().body("Error occurred when sending an email");
        }


    }

    private String generateEmailContent(Map<String,String> fields, NotificationType notificationType){
        Context context = new Context();
        // set placeholders inside the template
        for(Map.Entry<String,String> entry : fields.entrySet()){
            context.setVariable(entry.getKey(),entry.getValue());
        }
        // since the template name is lowercase of notificationType
        String templateName = notificationType.toString().toLowerCase();

        // return the email content with already filled placeholders
        return templateEngine.process(templateName, context);
    }
}