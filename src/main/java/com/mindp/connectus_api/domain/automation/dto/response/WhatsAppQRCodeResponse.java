package com.mindp.connectus_api.domain.automation.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record WhatsAppQRCodeResponse(
        UUID id,
        String qrCode,
        String status,
        LocalDateTime expiresAt) {
}
