package com.vinisnzy.connectus_api.domain.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Map;

@Builder
public record CreateCompanyRequest(
        @NotBlank String name,
        String cnpj
) {
}
