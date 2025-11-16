package com.vinisnzy.connectus_api.domain.analytics.dto.response.metrics;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserPerformanceResponse(
        UUID userId,
        String userName,
        Long totalTicketsHandled,
        Long ticketsResolved,
        Long salesCount,
        Double avgFirstResponseMinutes,
        Double avgResolutionMinutes,
        Integer activeDays
) {
}
