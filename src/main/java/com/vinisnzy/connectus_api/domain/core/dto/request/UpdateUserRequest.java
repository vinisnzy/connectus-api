package com.vinisnzy.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record UpdateUserRequest(
        String name,
        @Email String email,
        String phone,
        String avatar,
        Integer roleId,
        Boolean isActive
) {
}
