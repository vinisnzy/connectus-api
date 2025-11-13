package com.mindp.connectus_api.domain.core.dto.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
public record UpdateSubscriptionRequest(
        UUID planId,
        String billingPeriod,
        String status,
        BigDecimal discountPercentage,
        Map<String, Object> customLimits,
        Map<String, Object> customFeatures
) {
}
