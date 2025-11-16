package com.vinisnzy.connectus_api.domain.scheduling.dto.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdateServiceRequest(
        String name,
        String description,
        Integer durationMinutes,
        BigDecimal price,
        Integer bufferTimeMinutes,
        Boolean isActive
) {
}
