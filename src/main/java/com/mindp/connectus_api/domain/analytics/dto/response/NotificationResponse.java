package com.mindp.connectus_api.domain.analytics.dto.response;

import com.mindp.connectus_api.domain.analytics.entity.enums.NotificationType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record NotificationResponse (
        UUID id,
        NotificationType type,
        String title,
        String message
) {
}
