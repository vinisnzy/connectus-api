package com.vinisnzy.connectus_api.domain.messaging.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CreateTicketResponse(
        UUID id,
        String status,
        LocalDateTime createdAt) {
}
