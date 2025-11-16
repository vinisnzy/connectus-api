package com.vinisnzy.connectus_api.domain.messaging.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
public record MessageResponse(
        UUID id,
        String senderType,
        UUID senderId,
        String direction,
        String messageType,
        Map<String, Object> content,
        LocalDateTime sentAt,
        LocalDateTime deliveredAt,
        LocalDateTime readAt,
        Boolean isFromMe
) {
}
