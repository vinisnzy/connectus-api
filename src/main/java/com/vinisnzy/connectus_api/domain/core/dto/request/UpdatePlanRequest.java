package com.vinisnzy.connectus_api.domain.core.dto.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record UpdatePlanRequest(
        String displayName,
        String description,
        BigDecimal monthlyPrice,
        BigDecimal yearlyPrice,
        Map<String, Object> limits,
        Map<String, Object> features,
        Boolean isActive,
        Boolean isTrialEligible,
        Integer trialDays
) {
}
