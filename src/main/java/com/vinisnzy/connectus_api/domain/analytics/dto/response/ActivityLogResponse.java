package com.vinisnzy.connectus_api.domain.analytics.dto.response;

import com.vinisnzy.connectus_api.domain.core.dto.response.UserResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
public record ActivityLogResponse(
        UUID id,
        UserResponse user,
        String action,
        String entityType,
        UUID entityId,
        Map<String, Object> changes,
        String ipAddress,
        LocalDateTime createdAt) {
}
