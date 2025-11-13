package com.mindp.connectus_api.domain.crm.dto.request;

import lombok.Builder;

import java.util.Map;

@Builder
public record UpdateContactGroupRequest(
        String name,
        String description,
        Map<String, Object> conditions
) {
}
