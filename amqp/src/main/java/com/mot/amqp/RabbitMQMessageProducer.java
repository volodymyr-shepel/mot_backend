package com.mot.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQMessageProducer {
    private final AmqpTemplate amqpTemplate;

    @Autowired
    public RabbitMQMessageProducer(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void publish(Object payload, String exchange, String routingKey){
        log.info("publishing the message");
        amqpTemplate.convertAndSend(exchange,routingKey,payload);
        log.info("published message");
    }
}
