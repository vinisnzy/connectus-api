package com.mindp.connectus_api.domain.core.dto.response;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String avatar,
        String status,
        Boolean isActive,
        ZonedDateTime lastSeenAt,
        RoleResponse role,
        CompanyResponse company
) {
}
