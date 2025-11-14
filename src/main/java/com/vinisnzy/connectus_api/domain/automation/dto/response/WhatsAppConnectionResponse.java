package com.vinisnzy.connectus_api.domain.automation.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record WhatsAppConnectionResponse(
        UUID id,
        String connectionName,
        String phoneNumber,
        String status,
        LocalDateTime lastConnectedAt,
        LocalDateTime createdAt) {
}
