package com.vinisnzy.connectus_api.domain.automation.dto.request;

import lombok.Builder;

import java.util.Map;

@Builder
public record UpdateWebhookRequest(
        String name,
        String url,
        String[] events,
        Map<String, Object> headers,
        Map<String, Object> retryPolicy,
        Boolean isActive
) {
}
