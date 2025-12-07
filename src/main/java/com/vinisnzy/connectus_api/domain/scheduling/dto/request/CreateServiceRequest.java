package com.vinisnzy.connectus_api.domain.scheduling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateServiceRequest(
        @NotBlank(message = "Nome do serviço é obrigatório")
        String name,

        String description,

        @NotNull(message = "Duração do serviço em minutos é obrigatório")
        @PositiveOrZero(message = "Duração do serviço deve ser um valor positivo ou zero")
        Integer durationMinutes,

        @NotNull(message = "Preço do serviço é obrigatório")
        BigDecimal price,

        Integer bufferTimeMinutes
) {
}
