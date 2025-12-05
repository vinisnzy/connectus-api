package com.vinisnzy.connectus_api.domain.scheduling.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ServiceResponse(
        UUID id,
        String name,
        Integer duration,
        BigDecimal price,
        Boolean isActive
) {
}
