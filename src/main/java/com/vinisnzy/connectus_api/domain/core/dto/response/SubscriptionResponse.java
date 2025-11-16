package com.vinisnzy.connectus_api.domain.core.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record SubscriptionResponse(
        UUID id,
        PlanResponse plan,
        String status,
        String billingPeriod,
        BigDecimal price,
        BigDecimal discountPercentage,
        BigDecimal finalPrice,
        ZonedDateTime startedAt,
        ZonedDateTime expiresAt,
        ZonedDateTime trialEndsAt
) {
}
