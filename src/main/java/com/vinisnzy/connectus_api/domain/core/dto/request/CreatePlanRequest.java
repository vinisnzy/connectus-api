package com.vinisnzy.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record CreatePlanRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Nome de exibição é obrigatório")
        String displayName,

        String description,

        @NotNull(message = "Preço mensal é obrigatório")
        BigDecimal monthlyPrice,

        BigDecimal yearlyPrice,

        @NotNull(message = "Os limites são obrigatórios")
        Map<String, Object> limits,

        @NotNull(message = "As funcionalidades são obrigatórias")
        Map<String, Object> features,

        Boolean isTrialEligible,
        Integer trialDays
) {
}
