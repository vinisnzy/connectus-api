package com.vinisnzy.connectus_api.domain.analytics.dto.response.metrics;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ContactStatisticsResponse(
        Long totalTickets,
        Long resolvedTickets,
        Long totalAppointments,
        Long completedAppointments,
        BigDecimal totalSpent
) {
}
