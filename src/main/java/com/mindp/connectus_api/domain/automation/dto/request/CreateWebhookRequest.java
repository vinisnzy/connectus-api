package com.mindp.connectus_api.domain.automation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.UUID;

@Builder
public record CreateWebhookRequest(
        @NotNull UUID companyId,
        @NotBlank String name,
        @NotBlank String url,
        @NotEmpty String[] events,
        Map<String, Object> headers,
        Map<String, Object> retryPolicy
) {
}
