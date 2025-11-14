package com.vinisnzy.connectus_api.domain.core.dto.request;

import lombok.Builder;

import java.util.Map;

@Builder
public record UpdateCompanyRequest(
        String name,
        String cnpj,
        Map<String, Object> settings,
        Boolean isActive
) {
}
