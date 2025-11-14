package com.vinisnzy.connectus_api.domain.crm.dto.response;

import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record ContactGroupResponse(
        UUID id,
        String name,
        String description,
        Map<String, Object> conditions
) {
}
