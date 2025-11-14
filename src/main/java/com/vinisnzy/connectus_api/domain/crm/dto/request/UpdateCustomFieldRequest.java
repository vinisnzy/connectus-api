package com.vinisnzy.connectus_api.domain.crm.dto.request;

import lombok.Builder;

import java.util.Map;

@Builder
public record UpdateCustomFieldRequest(
        String label,
        String fieldType,
        Map<String, Object> options,
        Boolean isRequired,
        Map<String, Object> validationRules,
        String defaultValue,
        Integer sortOrder
) {
}
