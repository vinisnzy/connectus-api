package com.vinisnzy.connectus_api.domain.core.dto.request;

import lombok.Builder;

import java.util.Map;

@Builder
public record UpdateRoleRequest(
        String name,
        Map<String, Map<String, Boolean>> permissions
) {
}
