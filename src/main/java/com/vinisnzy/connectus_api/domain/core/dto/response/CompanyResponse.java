package com.vinisnzy.connectus_api.domain.core.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CompanyResponse(
        UUID id,
        String name,
        String cnpj
) {
}
