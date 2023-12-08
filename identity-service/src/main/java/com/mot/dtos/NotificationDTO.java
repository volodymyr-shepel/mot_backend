package com.mot.dtos;

import com.mot.enums.NotificationType;
import lombok.Builder;

import java.util.Map;

@Builder
public record NotificationDTO(
        String recipient,
        String subject,
        NotificationType notificationType,
        Map<String,String> notificationFields
){
}
