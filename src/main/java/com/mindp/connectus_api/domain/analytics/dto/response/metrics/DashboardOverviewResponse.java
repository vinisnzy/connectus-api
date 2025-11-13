package com.mindp.connectus_api.domain.analytics.dto.response.metrics;

import com.mindp.connectus_api.domain.dto.PeriodResponse;
import com.mindp.connectus_api.domain.messaging.dto.response.TicketMetricsResponse;
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
