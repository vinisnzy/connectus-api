package com.vinisnzy.connectus_api.domain.analytics.dto.response.metrics;

import lombok.Builder;

import java.util.List;

@Builder
public record DashboardOverviewResponse(
        PeriodResponse period,
        TicketMetricsResponse tickets,
        SalesMetricsResponse sales,
        AppointmentMetricsResponse appointments,
        List<UserPerformanceResponse> topUsers) {
}
