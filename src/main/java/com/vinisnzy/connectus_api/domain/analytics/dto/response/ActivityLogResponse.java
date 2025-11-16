package com.vinisnzy.connectus_api.domain.analytics.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ActivityLogResponse(
        UUID id,
        UUID companyId,
        String action,
        String entityType,
        LocalDateTime createdAt
) {
}
