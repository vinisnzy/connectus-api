package com.mindp.connectus_api.domain.crm.dto.response;

import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record CustomFieldResponse(
        UUID id,
        String name,
        String label,
        String fieldType,
        Map<String, Object> options,
        Boolean isRequired,
        Map<String, Object> validationRules,
        String defaultValue,
        Integer sortOrder
) {
}
