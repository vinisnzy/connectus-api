package com.vinisnzy.connectus_api.domain.crm.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
public record ContactResponse(
        UUID id,
        String phone,
        String name,
        String email,
        String profilePictureUrl,
        Boolean isBlocked,
        List<String> tags,
        Map<String, Object> customData,
        LocalDateTime lastInteractionAt,
        Long ticketsCount,
        Long appointmentsCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
