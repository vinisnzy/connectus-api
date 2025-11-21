package com.vinisnzy.connectus_api.domain.core.dto.request;

import com.vinisnzy.connectus_api.domain.core.entity.enums.BillingPeriod;
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
        BillingPeriod billingPeriod,

        Integer trialDays,
        BigDecimal discountPercentage,
        Map<String, Object> customLimits,
        Map<String, Object> customFeatures
) {
}
