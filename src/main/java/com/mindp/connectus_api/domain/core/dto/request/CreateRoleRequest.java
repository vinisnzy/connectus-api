package com.mindp.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record CreateRoleRequest(
        @NotNull UUID companyId,
        @NotBlank String name,
        @NotNull Map<String, Object> permissions
) {
}
