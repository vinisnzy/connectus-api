package com.vinisnzy.connectus_api.domain.analytics.dto.response.metrics;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SalesMetricsResponse(
        Long total,
        Long lost,
        Double conversionRate,
        BigDecimal totalRevenue) {
}
