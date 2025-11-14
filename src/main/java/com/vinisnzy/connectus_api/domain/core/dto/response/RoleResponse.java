package com.vinisnzy.connectus_api.domain.core.dto.response;

import lombok.Builder;

import java.util.Map;

@Builder
public record RoleResponse(
        Integer id,
        String name,
        Map<String, Object> permissions,
        Boolean isSystemRole
) {
}
