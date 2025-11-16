package com.vinisnzy.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
public record CreateSubscriptionRequest(
        @NotNull(message = "O id da empresa é obrigatório")
        UUID companyId,

        @NotNull(message = "O id do plano é obrigatório")
        UUID planId,

        @NotNull(message = "O período de cobrança é obrigatório")
        String billingPeriod,

        BigDecimal discountPercentage,
        Map<String, Object> customLimits,
        Map<String, Object> customFeatures
) {
}
