package com.mindp.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record CreatePlanRequest(
        @NotBlank String name,
        @NotBlank String displayName,
        String description,
        @NotNull BigDecimal monthlyPrice,
        BigDecimal yearlyPrice,
        @NotNull Map<String, Object> limits,
        @NotNull Map<String, Object> features,
        Integer sortOrder,
        Boolean isTrialEligible,
        Integer trialDays
) {
}
