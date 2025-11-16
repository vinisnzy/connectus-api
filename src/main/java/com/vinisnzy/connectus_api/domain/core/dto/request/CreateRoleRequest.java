package com.vinisnzy.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record CreateRoleRequest(
        @NotNull(message = "O id da empresa é obrigatório")
        UUID companyId,

        @NotBlank(message = "O nome do cargo é obrigatório")
        String name,

        @NotNull(message = "As permissões do cargo são obrigatórias")
        Map<String, Map<String, Boolean>> permissions
) {
}
