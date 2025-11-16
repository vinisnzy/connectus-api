package com.vinisnzy.connectus_api.domain.core.dto.response;

import com.vinisnzy.connectus_api.domain.core.entity.enums.UserStatus;
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
        UserStatus status,
        Boolean isActive,
        ZonedDateTime lastSeenAt,
        RoleResponse role,
        CompanyResponse company
) {
}
