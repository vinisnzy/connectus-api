package com.mindp.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
public record CreateSubscriptionRequest(
        @NotNull UUID companyId,
        @NotNull UUID planId,
        @NotNull String billingPeriod,
        BigDecimal discountPercentage,
        Map<String, Object> customLimits,
        Map<String, Object> customFeatures
) {
}
