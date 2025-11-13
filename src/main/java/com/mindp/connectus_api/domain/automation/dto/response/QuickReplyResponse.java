package com.mindp.connectus_api.domain.automation.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record QuickReplyResponse(
        UUID id,
        String shortcut,
        String title,
        String messageContent,
        String mediaUrl,
        Boolean isActive,
        Integer usageCount,
        LocalDateTime createdAt) {
}
