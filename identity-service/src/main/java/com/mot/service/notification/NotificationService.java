package com.mot.service.notification;

import com.mot.amqp.RabbitMQMessageProducer;
import com.mot.dtos.NotificationDTO;
import org.springframework.beans.factory.annotation.Value;

public abstract class NotificationService {
    @Value("${app.rabbitmq.internal-exchange}")
    private String internalExchange;

    @Value("${app.rabbitmq.email-routing-key}")
    private String emailRoutingKey;

    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public NotificationService(RabbitMQMessageProducer rabbitMQMessageProducer) {
        this.rabbitMQMessageProducer = rabbitMQMessageProducer;
    }

    public void publishNotification(NotificationDTO notification){
        rabbitMQMessageProducer.publish(
                notification,
                internalExchange,
                emailRoutingKey
        );
    }
}
