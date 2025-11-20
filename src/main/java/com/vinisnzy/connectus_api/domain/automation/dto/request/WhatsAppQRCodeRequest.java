package com.vinisnzy.connectus_api.domain.automation.dto.request;

import java.util.UUID;

public record WhatsAppQRCodeRequest(UUID companyId, UUID userId, UUID whatsAppConnectionId) {
}
