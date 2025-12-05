package com.vinisnzy.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdatePricingPlanRequest(
        @NotNull(message = "Preço mensal é obrigatório")
        BigDecimal monthlyPrice,

        BigDecimal yearlyPrice
) {
}
