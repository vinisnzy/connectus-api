package com.vinisnzy.connectus_api.domain.scheduling.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdateServiceRequest(
        String name,
        String description,

        @Positive(message = "Duração do serviço deve ser um valor positivo")
        Integer durationMinutes,

        @PositiveOrZero(message = "Preço do serviço deve ser um valor positivo ou zero")
        BigDecimal price,

        Integer bufferTimeMinutes,
        Boolean isActive
) {
}
