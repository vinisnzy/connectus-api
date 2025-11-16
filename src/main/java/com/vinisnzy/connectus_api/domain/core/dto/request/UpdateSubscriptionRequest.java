package com.vinisnzy.connectus_api.domain.core.dto.request;

import com.vinisnzy.connectus_api.domain.core.entity.enums.BillingPeriod;
import com.vinisnzy.connectus_api.domain.core.entity.enums.SubscriptionStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record UpdateSubscriptionRequest(
        UUID planId,
        BillingPeriod billingPeriod,
        SubscriptionStatus status,
        BigDecimal discountPercentage
) {
}
