package com.vinisnzy.connectus_api.domain.scheduling.dto.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record UpdateServiceRequest(
        String name,
        String description,
        Integer durationMinutes,
        BigDecimal price,
        Integer maxDailyAppointments,
        Integer bufferTimeMinutes,
        Map<String, Object> availability,
        Boolean isActive
) {
}
