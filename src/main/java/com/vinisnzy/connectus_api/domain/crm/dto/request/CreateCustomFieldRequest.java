package com.vinisnzy.connectus_api.domain.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record CreateCustomFieldRequest(
        @NotNull UUID companyId,
        @NotBlank String name,
        @NotBlank String label,
        @NotBlank String fieldType,
        Map<String, Object> options,
        Boolean isRequired,
        Map<String, Object> validationRules,
        String defaultValue,
        Integer sortOrder
) {
}
