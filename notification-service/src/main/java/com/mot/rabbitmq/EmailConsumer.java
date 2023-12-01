package com.mot.rabbitmq;


import com.mot.dtos.NotificationDTO;
import com.mot.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {
    private final EmailService emailService;

    @Autowired
    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${rabbitmq.queues.email}")
    public void consumer(NotificationDTO notification){
        emailService.sendNotification(notification);

    }
}
