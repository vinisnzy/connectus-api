package com.mindp.connectus_api.domain.core.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
public record PlanResponse(
        UUID id,
        String name,
        String displayName,
        String description,
        BigDecimal monthlyPrice,
        BigDecimal yearlyPrice,
        Map<String, Object> limits,
        Map<String, Object> features,
        Integer sortOrder,
        Boolean isActive,
        Boolean isTrialEligible,
        Integer trialDays
) {
}
