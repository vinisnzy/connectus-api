package com.vinisnzy.connectus_api.domain.analytics.dto.response.metrics;

import lombok.Builder;

@Builder
public record TicketMetricsResponse(
        Long total,
        Long open,
        Long pending,
        Long inProgress,
        Long resolved,
        Double avgFirstResponseMinutes,
        Double avgResolutionMinutes
) {
}
