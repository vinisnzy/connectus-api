package com.vinisnzy.connectus_api.domain.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record CreateContactGroupRequest(
        @NotNull UUID companyId,
        @NotBlank String name,
        String description,
        Map<String, Object> conditions
) {
}
