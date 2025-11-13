package com.mindp.connectus_api.domain.crm.dto.response;

import com.mindp.connectus_api.domain.messaging.dto.response.TicketResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
public record ContactDetailResponse(
        UUID id,
        String phone,
        String name,
        String email,
        String profilePictureUrl,
        Boolean isBlocked,
        List<String> tags,
        Map<String, Object> customData,
        LocalDateTime lastInteractionAt,
        LocalDateTime createdAt,
        ContactStatisticsResponse statistics,
        List<TicketResponse> recentTickets) {
}
