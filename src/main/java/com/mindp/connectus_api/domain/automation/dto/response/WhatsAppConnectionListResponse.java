package com.mindp.connectus_api.domain.automation.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record WhatsAppConnectionListResponse(
        List<WhatsAppConnectionResponse> connections) {
}
