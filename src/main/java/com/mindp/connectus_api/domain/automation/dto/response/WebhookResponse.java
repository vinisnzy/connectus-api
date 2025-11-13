package com.mindp.connectus_api.domain.automation.dto.response;

import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record WebhookResponse(
        UUID id,
        String name,
        String url,
        String[] events,
        Map<String, Object> headers,
        Map<String, Object> retryPolicy,
        Boolean isActive
) {
}
