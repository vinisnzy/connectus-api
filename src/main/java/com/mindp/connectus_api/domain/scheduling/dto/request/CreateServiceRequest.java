package com.mindp.connectus_api.domain.scheduling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
public record CreateServiceRequest(
        @NotNull UUID companyId,
        @NotBlank String name,
        String description,
        @NotNull Integer durationMinutes,
        BigDecimal price,
        Integer maxDailyAppointments,
        Integer bufferTimeMinutes,
        Map<String, Object> availability
) {
}
