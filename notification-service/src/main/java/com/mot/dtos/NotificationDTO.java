package com.mot.dtos;

import com.mot.util.NotificationType;

import java.util.Map;

public record NotificationDTO(
        String recipient,
        String subject,
        NotificationType notificationType,
        Map<String,String> notificationFields
){
}

