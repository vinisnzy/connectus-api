package com.vinisnzy.connectus_api.domain.analytics.dto.response.metrics;

import lombok.Builder;

@Builder
public record AppointmentMetricsResponse(
        Long total,
        Long scheduled,
        Long confirmed,
        Long completed,
        Long canceled,
        Long noShow
) {
}
