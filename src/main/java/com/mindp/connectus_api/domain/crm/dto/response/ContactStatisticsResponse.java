package com.mindp.connectus_api.domain.crm.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ContactStatisticsResponse(
        Long totalTickets,
        Long resolvedTickets,
        Long totalAppointments,
        Long completedAppointments,
        BigDecimal totalSpent) {
}
