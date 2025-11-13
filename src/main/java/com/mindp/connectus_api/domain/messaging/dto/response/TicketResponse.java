package com.mindp.connectus_api.domain.messaging.dto.response;

import com.mindp.connectus_api.domain.core.dto.response.UserResponse;
import com.mindp.connectus_api.domain.crm.dto.response.ContactResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
public record TicketResponse(
        UUID id,
        ContactResponse contact,
        UserResponse assignedUser,
        String status,
        String resolutionType,
        MessageResponse lastMessage,
        Long unreadCount,
        LocalDateTime pendingUntil,
        LocalDateTime firstResponseAt,
        LocalDateTime resolvedAt,
        Map<String, Object> metadata,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
