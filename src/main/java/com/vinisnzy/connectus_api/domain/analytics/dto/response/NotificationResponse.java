package com.vinisnzy.connectus_api.domain.analytics.dto.response;

import com.vinisnzy.connectus_api.domain.analytics.entity.enums.NotificationType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record NotificationResponse (
        UUID id,
        UUID userId,
        NotificationType type,
        String title,
        String message
) {
}
