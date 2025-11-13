package com.mindp.connectus_api.domain.core.dto.request;

import lombok.Builder;

import java.util.Map;

@Builder
public record UpdateRoleRequest(
        String name,
        Map<String, Object> permissions
) {
}
