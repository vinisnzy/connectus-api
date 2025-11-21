package com.vinisnzy.connectus_api.domain.crm.dto.request;

import lombok.Builder;

@Builder
public record UpdateContactGroupRequest(
        String name,
        String description
) {
}
